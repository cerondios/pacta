package com.pacta.pacta_app.shared.infrastructure.storage;

import com.pacta.pacta_app.shared.domain.PresignedDTO;
import com.pacta.pacta_app.shared.domain.StorageException;
import com.pacta.pacta_app.shared.domain.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;

@Service
@Profile({"dev", "prod"})
@RequiredArgsConstructor
class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public PresignedDTO upload(MultipartFile file) {
        String key = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            PutObjectResponse response = s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .getObjectRequest(getRequest)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest);
            return new PresignedDTO(presignedGetObjectRequest.url().toString(), key);
        } catch (IOException e) {
            throw new StorageException("Error uploading object to S3: " + key, e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception e) {
            throw new StorageException("Error deleting object from S3: " + key, e);
        }
    }

    @Override
    public PresignedDTO generatePresignedUpload(String resourceName) {
        String key = System.currentTimeMillis() + "-" + resourceName.replace(" ", "_");

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putRequest)
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();
        return new PresignedDTO(url, key);
    }
}
