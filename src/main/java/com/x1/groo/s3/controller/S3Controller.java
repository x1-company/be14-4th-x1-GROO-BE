package com.x1.groo.s3.controller;

import com.x1.groo.s3.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/image")
public class S3Controller {

    private final S3Service s3Service;

    @Autowired
    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/health2")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("I'm OK");
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<String> presignedUrl(@RequestParam String fileName) {
        String presignedUrl = s3Service.generatePresignedUrl(fileName);
        return ResponseEntity.ok(presignedUrl);
    }


}
