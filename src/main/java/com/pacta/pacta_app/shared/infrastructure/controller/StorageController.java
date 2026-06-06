package com.pacta.pacta_app.shared.infrastructure.controller;

import com.pacta.pacta_app.shared.domain.PresignedDTO;
import com.pacta.pacta_app.shared.domain.StorageException;
import com.pacta.pacta_app.shared.domain.StorageService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @ExceptionHandler(StorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorResponse handleStorageError(StorageException ex) {
        return new ErrorResponse(ex.getMessage());
    }

    public record UploadResponse(String url) {}

    record ErrorResponse(String error) {}
}
