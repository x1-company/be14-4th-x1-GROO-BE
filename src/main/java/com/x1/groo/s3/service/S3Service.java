package com.x1.groo.s3.service;

public interface S3Service {
    String generatePresignedUrl(String fileName);
}
