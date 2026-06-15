package com.pacta.pacta_app.shared.domain;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    PresignedDTO upload(MultipartFile file);
    /** Uploads server-generated content (e.g. a contract PDF) rather than a user-submitted file. */
    PresignedDTO uploadBytes(byte[] content, String filename, String contentType);
    void delete(String key);
    PresignedDTO generatePresignedUpload(String resourceName);
    String getViewUrl(String key);
}
