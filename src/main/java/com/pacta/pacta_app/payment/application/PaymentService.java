package com.pacta.pacta_app.payment.application;

import com.pacta.pacta_app.deal.domain.Deal;
import com.pacta.pacta_app.deal.domain.DealStatus;
import com.pacta.pacta_app.deal.domain.IDealRepository;
import com.pacta.pacta_app.payment.domain.IPaymentRepository;
import com.pacta.pacta_app.payment.domain.Payment;
import com.pacta.pacta_app.payment.domain.PaymentStatus;
import com.pacta.pacta_app.payment.application.dto.InitiatePaymentRequest;
import com.pacta.pacta_app.paymentgateway.domain.GatewayBankTransferRequest;
import com.pacta.pacta_app.paymentgateway.domain.GatewayTransaction;
import com.pacta.pacta_app.paymentgateway.domain.GatewayWebhookEvent;
import com.pacta.pacta_app.paymentgateway.domain.IPaymentGatewayService;
import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyRequest;
import com.pacta.pacta_app.property.domain.PropertyRequestStatus;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IPaymentRepository    payments;
    private final PropertyService       properties;
    private final IDealRepository       deals;
    private final IPaymentGatewayService gateway;
    private final MetricRecorder        metrics;
    private final IdGenerator           ids;

    @Transactional
    public Payment initiateBankTransferPayment(String tenantId, InitiatePaymentRequest req) {
        PropertyRequest propertyRequest = properties.findRequestById(req.propertyRequestId());
        if (!propertyRequest.getTenantId().equals(tenantId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your request");
        if (propertyRequest.getStatus() != PropertyRequestStatus.ACCEPTED)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Request is not accepted yet");

        Deal deal = deals.findByPropertyRequestId(propertyRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Contract not generated yet"));
        if (deal.getStatus() != DealStatus.ACTIVE)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Both parties must sign the contract before paying");

        Property property = properties.findById(propertyRequest.getPropertyId());
        if (property.getMonthlyRent() == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Property has no rent set");

        String reference = "pacta-" + propertyRequest.getId() + "-" + System.currentTimeMillis();

        GatewayTransaction tx = gateway.createBankTransferTransaction(new GatewayBankTransferRequest(
                property.getMonthlyRent(), property.getCurrency(), req.customerEmail(), reference,
                req.redirectUrl(), req.payerLegalIdType(), req.payerLegalId(), req.payerType(),
                req.bankCode(), "Arriendo Pacta - " + property.getId()));

        Payment payment = Payment.create(ids, tenantId, propertyRequest.getId(),
                property.getMonthlyRent(), property.getCurrency(), reference,
                tx.gatewayTransactionId(), tx.redirectUrl());
        payments.save(payment);
        metrics.incrementCounter("payment.initiated");
        return payment;
    }

    public List<Payment> findByTenantId(String tenantId) {
        return payments.findByTenantId(tenantId);
    }

    public Payment findById(String id) {
        return payments.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found: " + id));
    }

    public Payment findByIdForTenant(String id, String tenantId) {
        Payment payment = findById(id);
        if (!payment.getTenantId().equals(tenantId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return payment;
    }

    /** Payment history for a deal — visible to either the landlord or the tenant of that deal. */
    public List<Payment> findByDealId(String dealId, String callerId) {
        Deal deal = deals.findById(dealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deal not found: " + dealId));
        if (!callerId.equals(deal.getLandlordId()) && !callerId.equals(deal.getTenantId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return payments.findByPropertyRequestId(deal.getPropertyRequestId());
    }

    @Transactional
    public void handleWebhookEvent(Map<String, Object> payload) {
        gateway.parseWebhookEvent(payload).ifPresentOrElse(event ->
                payments.findByGatewayTransactionId(event.gatewayTransactionId()).ifPresentOrElse(payment -> {
                    PaymentStatus newStatus = PaymentStatus.valueOf(event.status().name());
                    payments.update(payment.withStatus(newStatus));
                    metrics.incrementCounter("payment.status_updated", "status", newStatus.name());
                }, () -> log.warn("Webhook for unknown gateway transaction {}", event.gatewayTransactionId())),
                () -> log.warn("Payment gateway webhook payload rejected or unparseable"));
    }
}
