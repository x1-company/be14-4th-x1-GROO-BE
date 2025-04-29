package com.x1.groo.diary.service;

import com.x1.groo.diary.dto.DiaryRequestDTO;

/**
 * 사용자 ID를 파라미터로 받아 일기를 생성
 */
public interface DiaryService {
    void createDiary(DiaryRequestDTO request, Long userId);
}