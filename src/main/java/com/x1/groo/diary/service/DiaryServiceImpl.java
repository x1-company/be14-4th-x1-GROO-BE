package com.x1.groo.diary.service;

import com.x1.groo.ai.dto.EmotionRequestDTO;
import com.x1.groo.ai.dto.EmotionResponseDTO;
import com.x1.groo.ai.service.EmotionService;
import com.x1.groo.diary.dto.DiaryRequestDTO;
import com.x1.groo.diary.entity.Diary;
import com.x1.groo.diary.entity.DiaryEmotion;
import com.x1.groo.diary.repository.DiaryEmotionRepository;
import com.x1.groo.diary.repository.DiaryRepository;
import com.x1.groo.forest.emotion.command.domain.aggregate.ForestEntity;
import com.x1.groo.forest.emotion.command.domain.repository.ForestRepository;
import com.x1.groo.forest.emotion.command.domain.repository.EmotionSharedForestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DiaryService 구현체
 * 일기 저장, AI 감정 분석, 상위 2개 감정 DB 저장 및 날씨 업데이트를 수행합니다.
 */
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;
    private final DiaryEmotionRepository emotionRepo;
    private final EmotionService emotionService;
    private final ForestRepository forestRepo;
    private final EmotionSharedForestRepository sharedForestRepo;

    @Override
    @Transactional
    public void createDiary(DiaryRequestDTO req, int userId) {
        // 0) 권한 체크
        Integer forestId = req.getForestId();
        boolean owner = forestRepo.findById(forestId)
                .map((ForestEntity f) -> f.getUser().getId() == userId)  // ← getUser().getId() 사용
                .orElse(false);

        boolean shared = sharedForestRepo.existsByUserIdAndForestId(userId, forestId);

        if (!(owner || shared)) {
            throw new AccessDeniedException(
                    String.format("사용자[%d]가 숲[%d]에 대한 쓰기 권한이 없습니다.", userId, forestId)
            );
        }


        // 1) Diary 엔티티 생성 및 필수 값 세팅
        Diary diary = new Diary();
        diary.setContent(req.getContent());
        diary.setIsPublished(req.isPublished());
        diary.setUserId(userId);
        diary.setForestId(req.getForestId());
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());

        // 2) AI 감정 분석 호출
        EmotionResponseDTO aiRes = emotionService.analyzeEmotion(
                new EmotionRequestDTO(req.getContent())
        );

        String mainEmotion = aiRes.getMainEmotion()
                .trim()
                .replaceAll("[\"\\r\\n]", "");

        // 3) 날씨 세팅 (INSERT 시점에 함께 저장)
        diary.setWeather(aiRes.getWeather());

        // 4) Diary INSERT
        final Diary savedDiary = diaryRepo.save(diary);

        // 5) 상위 2개 감정만 DB 저장
        LinkedHashMap<String, Integer> top2 = aiRes.getEmotionResult().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(2)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldVal, newVal) -> oldVal,
                        LinkedHashMap::new
                ));

        top2.forEach((emotion, weight) -> {
            DiaryEmotion de = new DiaryEmotion();
            de.setDiary(savedDiary);
            de.setEmotion(emotion);
            de.setWeight(weight);
            emotionRepo.save(de);
        });
    }
}