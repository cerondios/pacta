package com.pacta.pacta_app.property.application;

import com.pacta.pacta_app.admin.domain.IAdminRepository;
import com.pacta.pacta_app.property.application.dto.PropertyAttributeConfigPatchRequest;
import com.pacta.pacta_app.property.application.dto.PropertyAttributeConfigRequest;
import com.pacta.pacta_app.property.domain.IPropertyAttributeConfigRepository;
import com.pacta.pacta_app.property.domain.PropertyAttributeConfig;
import com.pacta.pacta_app.shared.domain.DateUtil;
import com.pacta.pacta_app.shared.domain.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyAttributeConfigService {

    private final IPropertyAttributeConfigRepository configs;
    private final IAdminRepository                   admins;
    private final IdGenerator                        ids;

    /** Public — returns only enabled configs for the given type plus ALL-type configs. */
    public List<PropertyAttributeConfig> findEnabled(String propertyType) {
        if (propertyType != null && !propertyType.isBlank())
            return configs.findEnabledByPropertyType(propertyType);
        return configs.findEnabled();
    }

    /** Admin — full list including disabled. */
    public List<PropertyAttributeConfig> findAll(String operatorId) {
        requireManageConfig(operatorId);
        return configs.findAll();
    }

    public PropertyAttributeConfig create(String operatorId, PropertyAttributeConfigRequest req) {
        requireManageConfig(operatorId);
        return configs.save(PropertyAttributeConfig.builder()
                .id(ids.generate())
                .propertyType(req.propertyType().toUpperCase())
                .category(req.category().toUpperCase())
                .displayName(req.displayName())
                .enabled(true)
                .createdBy(operatorId)
                .createdAt(DateUtil.now())
                .build());
    }

    public PropertyAttributeConfig patch(String operatorId, String id, PropertyAttributeConfigPatchRequest req) {
        requireManageConfig(operatorId);
        PropertyAttributeConfig existing = configs.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found: " + id));
        var updated = existing.toBuilder();
        if (req.displayName() != null) updated.displayName(req.displayName());
        if (req.enabled()     != null) updated.enabled(req.enabled());
        return configs.save(updated.build());
    }

    public void delete(String operatorId, String id) {
        requireManageConfig(operatorId);
        if (configs.findById(id).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Config not found: " + id);
        configs.delete(id);
    }

    private void requireManageConfig(String operatorId) {
        var admin = admins.findById(operatorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Operator not found: " + operatorId));
        if (!admin.canManageConfig())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only ADMIN or SUPER_ADMIN can manage property attribute configs");
    }
}
