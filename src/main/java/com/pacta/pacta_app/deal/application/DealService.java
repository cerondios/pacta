package com.pacta.pacta_app.deal.application;

import com.pacta.pacta_app.deal.domain.Deal;
import com.pacta.pacta_app.deal.domain.IDealRepository;
import com.pacta.pacta_app.property.application.PropertyService;
import com.pacta.pacta_app.property.domain.Property;
import com.pacta.pacta_app.property.domain.PropertyRequest;
import com.pacta.pacta_app.property.domain.PropertyRequestStatus;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.MetricRecorder;
import com.pacta.pacta_app.shared.domain.StorageService;
import com.pacta.pacta_app.user.domain.IUserRepository;
import com.pacta.pacta_app.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DealService {

    private final IDealRepository       deals;
    private final IUserRepository       users;
    private final PropertyService       properties;
    private final ContractPdfGenerator  contractPdfGenerator;
    private final StorageService        storage;
    private final MetricRecorder        metrics;
    private final IdGenerator           ids;

    /** Called right after a landlord accepts a tenant's property request. */
    @Transactional
    public Deal createFromAcceptedRequest(PropertyRequest request, Property property) {
        User landlord = requireUser(property.getLandlordId());
        User tenant = requireUser(request.getTenantId());

        byte[] pdf = contractPdfGenerator.generate(property, landlord, tenant);
        var uploaded = storage.uploadBytes(pdf, "contrato-" + request.getId() + ".pdf", "application/pdf");

        Deal deal = Deal.create(ids, request.getId(), property.getId(),
                property.getLandlordId(), request.getTenantId(), uploaded.key());
        deals.save(deal);
        metrics.incrementCounter("deal.created");
        return deal;
    }

    /**
     * "Mis negocios": every deal where the caller is either the landlord or the tenant.
     * Self-heals first — any ACCEPTED request the caller is party to that predates this
     * feature (or otherwise never got a deal) gets one created on the fly.
     */
    @Transactional
    public List<Deal> findMyDeals(String userId) {
        backfillMissingDeals(userId);
        List<Deal> merged = new ArrayList<>(deals.findByLandlordId(userId));
        merged.addAll(deals.findByTenantId(userId));
        return merged;
    }

    private void backfillMissingDeals(String userId) {
        List<PropertyRequest> tenantAccepted = properties.findMyRequests(userId).stream()
                .filter(r -> r.getStatus() == PropertyRequestStatus.ACCEPTED)
                .toList();
        List<PropertyRequest> landlordAccepted = properties.findAcceptedRequestsForLandlord(userId);

        Stream.concat(tenantAccepted.stream(), landlordAccepted.stream())
                .forEach(this::ensureDealExists);
    }

    private void ensureDealExists(PropertyRequest request) {
        if (deals.findByPropertyRequestId(request.getId()).isPresent()) return;
        try {
            Property property = properties.findById(request.getPropertyId());
            createFromAcceptedRequest(request, property);
        } catch (Exception e) {
            log.warn("Could not backfill deal for accepted request {}", request.getId(), e);
        }
    }

    public Deal findByIdForUser(String dealId, String callerId) {
        Deal deal = requireDeal(dealId);
        requireParty(deal, callerId);
        return deal;
    }

    public String getContractViewUrl(String dealId, String callerId) {
        Deal deal = findByIdForUser(dealId, callerId);
        return storage.getViewUrl(deal.getContractFileKey());
    }

    @Transactional
    public Deal sign(String dealId, String callerId, String signatureName) {
        Deal deal = requireDeal(dealId);
        requireParty(deal, callerId);

        Deal signed;
        try {
            if (callerId.equals(deal.getLandlordId())) {
                signed = deal.signAsLandlord(signatureName);
            } else {
                signed = deal.signAsTenant(signatureName);
            }
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }

        deals.update(signed);
        metrics.incrementCounter("deal.signed");
        return signed;
    }

    private void requireParty(Deal deal, String callerId) {
        if (!callerId.equals(deal.getLandlordId()) && !callerId.equals(deal.getTenantId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    private Deal requireDeal(String dealId) {
        return deals.findById(dealId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deal not found: " + dealId));
    }

    private User requireUser(String userId) {
        return users.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId));
    }
}
