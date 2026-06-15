package com.pacta.pacta_app.payment.infrastructure.controller;

import com.pacta.pacta_app.payment.application.PaymentService;
import com.pacta.pacta_app.payment.application.dto.InitiatePaymentRequest;
import com.pacta.pacta_app.payment.application.dto.PaymentResponse;
import com.pacta.pacta_app.paymentgateway.domain.GatewayBank;
import com.pacta.pacta_app.paymentgateway.domain.IPaymentGatewayService;
import com.pacta.pacta_app.shared.infrastructure.filter.PactaTokenFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService         paymentService;
    private final IPaymentGatewayService gateway;

    /** Banks the tenant can pick from when paying by direct bank transfer. */
    @GetMapping("/banks")
    public List<GatewayBank> getAvailableBanks() {
        return gateway.listAvailableBanks();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse initiate(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String tenantId,
            @Valid @RequestBody InitiatePaymentRequest req) {
        return PaymentResponse.from(paymentService.initiateBankTransferPayment(tenantId, req));
    }

    @GetMapping("/my-payments")
    public List<PaymentResponse> myPayments(
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String tenantId) {
        return paymentService.findByTenantId(tenantId).stream().map(PaymentResponse::from).toList();
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(
            @PathVariable String id,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String tenantId) {
        return PaymentResponse.from(paymentService.findByIdForTenant(id, tenantId));
    }

    /** Payment history for a deal — visible to either party (tenant who pays, landlord who gets paid). */
    @GetMapping("/by-deal/{dealId}")
    public List<PaymentResponse> findByDeal(
            @PathVariable String dealId,
            @RequestHeader(PactaTokenFilter.HEADER_USER_ID) String userId) {
        return paymentService.findByDealId(dealId, userId).stream().map(PaymentResponse::from).toList();
    }

    /** The payment gateway calls this asynchronously to report transaction status changes. Public — verified via signature. */
    @PostMapping("/webhook")
    public void webhook(@RequestBody Map<String, Object> payload) {
        paymentService.handleWebhookEvent(payload);
    }
}
