package com.x1.groo.diary.service;

import com.x1.groo.ai.dto.EmotionRequestDTO;
import com.x1.groo.ai.dto.EmotionResponseDTO;
import com.x1.groo.ai.service.EmotionService;
import com.x1.groo.diary.dto.DiaryRequestDTO;
import com.x1.groo.diary.entity.Diary;
import com.x1.groo.diary.entity.DiaryEmotion;
import com.x1.groo.diary.repository.DiaryEmotionRepository;
import com.x1.groo.diary.repository.DiaryRepository;
import com.x1.groo.security.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DiaryService 구현체
 * 일기 등록 + AI 감정 분석 + DB 저장 로직을 수행
 */
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;
    private final DiaryEmotionRepository emotionRepo;
    private final EmotionService emotionService;

    @Override
    @Transactional
    public void createDiary(DiaryRequestDTO req) {
        // 1) 로그인된 사용자 ID 추출
        Long userId = SecurityUtil.getCurrentMemberId();

        // 2) Diary 저장 (새 final 변수에 담기)
        Diary diaryEntity = new Diary();
        diaryEntity.setContent(req.getContent());
        diaryEntity.setIsPublished(req.isPublished());
        diaryEntity.setUserId(userId);
        diaryEntity.setForestId(req.getForestId());
        final Diary savedDiary = diaryRepo.save(diaryEntity);

        // 3) AI 감정 분석 호출
        EmotionResponseDTO aiRes = emotionService.analyzeEmotion(
                new EmotionRequestDTO(req.getContent())
        );

        // 4) 상위 2개 감정만 DB 저장
        LinkedHashMap<String, Integer> top2 = aiRes.getEmotionResult().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(2)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));

        // 람다 내에서는 final 변수인 savedDiary 사용
        top2.forEach((emotion, weight) -> {
            DiaryEmotion de = new DiaryEmotion();
            de.setDiary(savedDiary);
            de.setEmotion(emotion);
            de.setWeight(weight);
            emotionRepo.save(de);
        });

        // 5) 날씨 업데이트
        savedDiary.setWeather(aiRes.getWeather());
        diaryRepo.save(savedDiary);
    }
}