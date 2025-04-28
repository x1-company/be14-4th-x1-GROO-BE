package com.x1.groo.diary.controller;

import com.x1.groo.diary.dto.DiaryRequestDTO;
import com.x1.groo.diary.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 일기 등록 API 엔드포인트
 */
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody DiaryRequestDTO req) {
        diaryService.createDiary(req);
        return ResponseEntity.noContent().build();
    }
}