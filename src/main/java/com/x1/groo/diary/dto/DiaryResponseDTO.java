package com.x1.groo.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponseDTO {
    private int diaryId;
    private int userId;
    private int forestId;
    private Map<String, Integer> topEmotions;
    private String mainEmotion;
    private String weather;
    private String content;
}