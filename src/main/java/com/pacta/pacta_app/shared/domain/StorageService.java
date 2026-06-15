package com.pacta.pacta_app.shared.domain;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    PresignedDTO upload(MultipartFile file);
    void delete(String key);
    PresignedDTO generatePresignedUpload(String resourceName);
    String getViewUrl(String key);
}
