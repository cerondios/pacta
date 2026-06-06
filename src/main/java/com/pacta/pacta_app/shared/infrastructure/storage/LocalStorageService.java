package com.pacta.pacta_app.shared.infrastructure.storage;

import com.pacta.pacta_app.shared.domain.IdGenerator;
import com.pacta.pacta_app.shared.domain.PresignedDTO;
import com.pacta.pacta_app.shared.domain.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Profile({"local", "default"})
@RequiredArgsConstructor
class LocalStorageService implements StorageService {

    private final IdGenerator ids;

    @Override
    public PresignedDTO upload(MultipartFile file) {
        String key = ids.generate() + "-" + file.getOriginalFilename();
        log.info("[storage] upload: {}", key);
        return new PresignedDTO("http://localhost/files/", key);
    }

    @Override
    public void delete(String key) {
        log.info("[storage] delete: {}", key);
    }

    @Override
    public PresignedDTO generatePresignedUpload(String resourceName) {
        String key = ids.generate() + "-" + resourceName.replace(" ", "_");
        log.info("[storage] presign: {}", key);
        return new PresignedDTO("http://localhost/files/" + key, key);
    }
}
