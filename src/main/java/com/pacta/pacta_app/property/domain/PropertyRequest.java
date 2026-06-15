package com.pacta.pacta_app.property.domain;

import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class PropertyRequest {

    private final String id;
    private final String tenantId;
    private final String propertyId;
    private final PropertyRequestStatus status;
    private final String appliedAt;
    private final String reviewedAt;
    private final String reviewedBy;

    public static PropertyRequest create(IdGenerator ids, String tenantId, String propertyId) {
        return PropertyRequest.builder()
                .id(ids.generate())
                .tenantId(tenantId)
                .propertyId(propertyId)
                .status(PropertyRequestStatus.PENDING)
                .appliedAt(DateUtil.now())
                .build();
    }

    public PropertyRequest accept(String reviewedBy) {
        return toBuilder()
                .status(PropertyRequestStatus.ACCEPTED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }

    public PropertyRequest reject(String reviewedBy) {
        return toBuilder()
                .status(PropertyRequestStatus.REJECTED)
                .reviewedBy(reviewedBy)
                .reviewedAt(DateUtil.now())
                .build();
    }
}
