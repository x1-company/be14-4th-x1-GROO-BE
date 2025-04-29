package com.x1.groo.diary.service;

import com.x1.groo.diary.dto.DiaryRequestDTO;
import com.x1.groo.diary.dto.DiaryResponseDTO;
import com.x1.groo.diary.dto.DiarySaveRequestDTO;
import com.x1.groo.diary.dto.DiarySaveResponseDTO;

/**
 * 사용자 ID를 파라미터로 받아 일기를 생성
 */
public interface DiaryService {
    /** 정식 등록 (AI 감정분석, 날씨, 상위2개 감정 저장) */
    DiaryResponseDTO createDiary(DiaryRequestDTO request, int userId);
    /** 임시 저장 (AI 호출 없이 content만 저장) */
    DiarySaveResponseDTO saveDiary(DiarySaveRequestDTO request, int userId);
}