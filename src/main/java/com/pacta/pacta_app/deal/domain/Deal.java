package com.pacta.pacta_app.deal.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

/**
 * A rental deal ("negocio") created the moment a landlord accepts a tenant's
 * property request. It only becomes {@link DealStatus#ACTIVE} once both the
 * landlord and the tenant have digitally signed the generated contract.
 */
@Getter
@Builder(toBuilder = true)
public class Deal {

    private final String id;
    private final String propertyRequestId;
    private final String propertyId;
    private final String landlordId;
    private final String tenantId;
    private final DealStatus status;
    private final String contractFileKey;

    private final String landlordSignedAt;
    private final String landlordSignatureName;
    private final String tenantSignedAt;
    private final String tenantSignatureName;

    private final String createdAt;
    private final String updatedAt;

    public static Deal create(IdGenerator ids, String propertyRequestId, String propertyId,
                              String landlordId, String tenantId, String contractFileKey) {
        return Deal.builder()
                .id(ids.generate())
                .propertyRequestId(propertyRequestId)
                .propertyId(propertyId)
                .landlordId(landlordId)
                .tenantId(tenantId)
                .status(DealStatus.PENDING_SIGNATURES)
                .contractFileKey(contractFileKey)
                .createdAt(DateUtil.now())
                .build();
    }

    public boolean isLandlordSigned() {
        return landlordSignedAt != null;
    }

    public boolean isTenantSigned() {
        return tenantSignedAt != null;
    }

    public Deal signAsLandlord(String signatureName) {
        if (isLandlordSigned())
            throw new IllegalStateException("Landlord already signed this deal");
        return toBuilder()
                .landlordSignedAt(DateUtil.now())
                .landlordSignatureName(signatureName)
                .updatedAt(DateUtil.now())
                .build()
                .promoteIfFullySigned();
    }

    public Deal signAsTenant(String signatureName) {
        if (isTenantSigned())
            throw new IllegalStateException("Tenant already signed this deal");
        return toBuilder()
                .tenantSignedAt(DateUtil.now())
                .tenantSignatureName(signatureName)
                .updatedAt(DateUtil.now())
                .build()
                .promoteIfFullySigned();
    }

    private Deal promoteIfFullySigned() {
        if (isLandlordSigned() && isTenantSigned()) {
            return toBuilder().status(DealStatus.ACTIVE).build();
        }
        return this;
    }
}
