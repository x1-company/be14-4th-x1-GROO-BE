package com.x1.groo.diary.dto;

import lombok.Data;

/**
 * 일기 등록 요청 DTO
 */
@Data
public class DiaryRequestDTO {
    private Long forestId;
    private String content;
    private boolean isPublished = true;
}