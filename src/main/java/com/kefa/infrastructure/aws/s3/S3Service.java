package com.kefa.infrastructure.aws.s3;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.S3FileUploadException;
import com.kefa.infrastructure.aws.config.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties properties;

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Set.of("jpg", "jpeg", "png", "gif", "webp"));

    public List<String> uploadFile(List<MultipartFile> files) {

        validateFiles(files);

        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {

            String originalFilename = file.getOriginalFilename();

            validateOriginalFilename(originalFilename);
            validateFileExtension(originalFilename);

            String uniqueFileName = generateUniqueFileName(originalFilename);
            String fileUrl = s3Upload(file, uniqueFileName);
            fileUrls.add(fileUrl);

        }

        return fileUrls;
    }

    public void deleteFile(String keyName) {

        try {

            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(properties.getS3().getBucket())
                .key(keyName)
                .build();
            s3Client.deleteObject(deleteRequest);

        } catch (Exception e) {
            throw new S3FileUploadException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    private void validateOriginalFilename(String originalFilename) {

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new S3FileUploadException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private void validateFiles(List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            throw new S3FileUploadException(ErrorCode.EMPTY_FILE);
        }

        for (MultipartFile file : files) {

            if (file.isEmpty()) {
                throw new S3FileUploadException(ErrorCode.EMPTY_FILE);
            }
            validateOriginalFilename(file.getOriginalFilename());

        }
    }

    private void validateFileExtension(String originalFilename) {

        int lastDotIndex = originalFilename.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            throw new S3FileUploadException(ErrorCode.INVALID_FILE_TYPE);
        }

        String fileExtension = originalFilename.substring(lastDotIndex + 1).trim().toLowerCase();

        if (!SUPPORTED_EXTENSIONS.contains(fileExtension)) {
            throw new S3FileUploadException(ErrorCode.INVALID_FILE_TYPE);
        }

    }

    private String s3Upload(MultipartFile file, String uniqueFileName) {

        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(properties.getS3().getBucket())
            .key(uniqueFileName)
            .build();

        try (InputStream inputStream = file.getInputStream()) {

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
            return generateFileUrl(uniqueFileName);

        } catch (Exception e) {
            throw new S3FileUploadException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private String generateUniqueFileName(String originalFileName) {

        int lastDotIndex = originalFileName.lastIndexOf('.');

        if (lastDotIndex == -1 || lastDotIndex == originalFileName.length() - 1) {
            throw new S3FileUploadException(ErrorCode.INVALID_FILE_TYPE);
        }

        String fileName = originalFileName.substring(0, lastDotIndex).trim();
        String extension = originalFileName.substring(lastDotIndex).toLowerCase();

        return String.format("uploads/%s_%s%s", Instant.now().toEpochMilli(), fileName, extension);

    }

    private String generateFileUrl(String uniqueFileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", properties.getS3().getBucket(), properties.getRegion(), uniqueFileName);
    }
}
