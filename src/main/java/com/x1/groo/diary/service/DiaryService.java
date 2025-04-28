package com.x1.groo.diary.service;

import com.x1.groo.diary.dto.DiaryRequestDTO;

/**
 * 일기 등록 기능만 수행하고, 반환값 없이 저장만 처리
 */
public interface DiaryService {
    void createDiary(DiaryRequestDTO request);
}