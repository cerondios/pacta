package com.pacta.pacta_app.shared.infrastructure.controller;

import com.pacta.pacta_app.shared.domain.PresignedDTO;
import com.pacta.pacta_app.shared.domain.StorageService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storage;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PresignedDTO upload(@RequestPart("file") MultipartFile file) {
        return storage.upload(file);
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String key) {
        storage.delete(key);
    }

    @GetMapping("/presign")
    public PresignedDTO presign(@RequestParam @NotBlank String name) {
        return storage.generatePresignedUpload(name);
    }

    @GetMapping("/url")
    public Map<String, String> getViewUrl(@RequestParam @NotBlank String key) {
        return Map.of("url", storage.getViewUrl(key));
    }
}
