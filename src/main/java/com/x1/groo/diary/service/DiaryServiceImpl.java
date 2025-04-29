package com.x1.groo.diary.service;

import com.x1.groo.ai.dto.EmotionRequestDTO;
import com.x1.groo.ai.dto.EmotionResponseDTO;
import com.x1.groo.ai.service.EmotionService;
import com.x1.groo.diary.dto.*;
import com.x1.groo.diary.entity.Diary;
import com.x1.groo.diary.entity.DiaryEmotion;
import com.x1.groo.diary.repository.DiaryEmotionRepository;
import com.x1.groo.diary.repository.DiaryRepository;
import com.x1.groo.forest.emotion.command.domain.repository.ForestRepository;
import com.x1.groo.forest.emotion.command.domain.repository.EmotionSharedForestRepository;
import com.x1.groo.item.dto.CategoryEmotionItemDTO;
import com.x1.groo.item.service.ItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    private final DiaryRepository diaryRepo;
    private final DiaryEmotionRepository emotionRepo;
    private final EmotionService emotionService;
    private final ForestRepository forestRepo;
    private final EmotionSharedForestRepository sharedForestRepo;
    private final ItemService itemService;

    @Override
    @Transactional
    public DiaryResponseDTO createDiary(DiaryRequestDTO req, int userId) {
        int forestId = req.getForestId();
        int categoryId = req.getCategoryId();
        // 권한 체크
        boolean owner = forestRepo.findById(forestId)
                .map(f -> f.getUser().getId() == userId)
                .orElse(false);
        boolean shared = sharedForestRepo.existsByUserIdAndForestId(userId, forestId);
        if (!(owner || shared)) {
            throw new AccessDeniedException(
                    String.format("사용자[%d]가 숲[%d]에 대한 쓰기 권한이 없습니다.", userId, forestId)
            );
        }

        // AI 감정 분석
        EmotionResponseDTO aiRes = emotionService.analyzeEmotion(
                new EmotionRequestDTO(req.getContent())
        );
        String mainEmotion = aiRes.getMainEmotion()
                .trim()
                .replaceAll("[\"\\r\\n]", "");

        String weather = aiRes.getWeather();

        // Diary 저장
        Diary diary = new Diary();
        diary.setContent(req.getContent());
        diary.setIsPublished(true);
        diary.setUserId(userId);
        diary.setForestId(forestId);
        diary.setWeather(weather);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        Diary savedDiary = diaryRepo.save(diary);

        // 상위 2개 감정 추출 및 저장
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

        // 감정 기반 아이템 조회
        List<CategoryEmotionItemDTO> emotionItems = 
          itemService.findItemsByCategoryAndEmotion(categoryId, mainEmotion);
      
        // DTO 반환
        return new DiaryResponseDTO(
                savedDiary.getId(),
                userId,
                forestId,
                top2,
                mainEmotion,
                weather,          // aiRes.getWeather() 값
                req.getContent(),
                emotionItems
        );
    }

    @Override
    @Transactional
    public DiarySaveResponseDTO saveDiary(DiarySaveRequestDTO req, int userId) {
        int forestId = req.getForestId();
        boolean owner = forestRepo.findById(forestId)
                .map(f -> f.getUser().getId() == userId)
                .orElse(false);
        boolean shared = sharedForestRepo.existsByUserIdAndForestId(userId, forestId);
        if (!(owner || shared)) {
            throw new AccessDeniedException(
                    String.format("사용자[%d]가 숲[%d]에 대한 권한이 없습니다.", userId, forestId)
            );
        }

        // 임시 저장
        Diary diary = new Diary();
        diary.setContent(req.getContent());
        diary.setIsPublished(false);
        diary.setUserId(userId);
        diary.setForestId(forestId);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diaryRepo.save(diary);

        return new DiarySaveResponseDTO(req.getContent());
    }

    // 임시 저장 조회
    @Override
    @Transactional
    public List<DiarySaveInfoDTO> getSaves(int userId) {
        return diaryRepo.findAllByUserIdAndIsPublishedFalse(userId).stream()
                .map(d -> new DiarySaveInfoDTO(
                        d.getId(),
                        d.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}