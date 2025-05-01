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
import org.springframework.cglib.core.Local;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;


import java.time.LocalDateTime;
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
        LocalDateTime createdAt = req.getCreatedAt();


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
        diary.setCreatedAt(createdAt);
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

        List<CategoryEmotionItemDTO> emotionItems =
          itemService.findItemsByCategoryAndEmotion(categoryId, mainEmotion);

        return new DiaryResponseDTO(
                savedDiary.getId(),
                userId,
                forestId,
                top2,
                mainEmotion,
                weather,
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

    // 임시 저장 상세 조회
    @Override
    @Transactional
    public DiarySaveDetailDTO getSaveDetail(int userId, int diaryId) {
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시 저장입니다."));
        if (diary.getUserId() != userId || diary.getIsPublished()) {
            throw new AccessDeniedException("해당 임시 저장에 접근할 권한이 없습니다.");
        }
        return new DiarySaveDetailDTO(
                diary.getId(),
                diary.getContent(),
                diary.getCreatedAt()
        );
    }

    // 임시 저장 일기 수정
    @Override
    @Transactional
    public DiarySaveUpdateResponseDTO updateSave(int userId, int diaryId, DiarySaveRequestDTO req) {
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시 저장입니다."));

        if (diary.getUserId() != userId || diary.getIsPublished()) {
            throw new AccessDeniedException("해당 임시 저장을 수정할 권한이 없습니다.");
        }

        diary.setContent(req.getContent());
        diary.setUpdatedAt(LocalDateTime.now());
        diaryRepo.save(diary);

        return new DiarySaveUpdateResponseDTO(
                diary.getContent(),
                diary.getUpdatedAt()
        );
    }

    // 임시 저장 일기 삭제
    @Override
    @Transactional
    public void deleteSave(int userId, int diaryId) {
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 임시 저장입니다."));
        if (diary.getUserId() != userId || diary.getIsPublished()) {
            throw new AccessDeniedException("해당 임시 저장을 삭제할 권한이 없습니다.");
        }
        diaryRepo.delete(diary);
    }

    // 임시 저장된 일기 등록
    @Override
    @Transactional
    public DiaryResponseDTO publishSave(int userId, int diaryId) {
        Diary d = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("없음"));
        if (d.getUserId() != userId || d.getIsPublished()) {
            throw new AccessDeniedException("권한 없음");
        }
        EmotionResponseDTO aiRes = emotionService.analyzeEmotion(
                new EmotionRequestDTO(d.getContent())
        );
        String mainEmotion = aiRes.getMainEmotion().trim();
        String weather = aiRes.getWeather();

        d.setIsPublished(true);
        d.setWeather(weather);
        d.setUpdatedAt(LocalDateTime.now());
        diaryRepo.save(d);

        Map<String, Integer> top2 = aiRes.getEmotionResult().entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(2)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (o, n) -> o,
                        LinkedHashMap::new
                ));
        top2.forEach((emotion, weight) -> {
            DiaryEmotion de = new DiaryEmotion();
            de.setDiary(d);
            de.setEmotion(emotion);
            de.setWeight(weight);
            emotionRepo.save(de);
        });

        List<CategoryEmotionItemDTO> emotionItems = Collections.emptyList();

        return new DiaryResponseDTO(
                d.getId(),
                userId,
                d.getForestId(),
                top2,
                mainEmotion,
                weather,
                d.getContent(),
                emotionItems
        );
    }

    // 일기 수정
    @Override
    @Transactional
    public DiaryUpdateResponseDTO updateDiary(DiaryUpdateRequestDTO req, int userId) {
        int diaryId = req.getDiaryId();
        int forestId = req.getForestId();
        String content = req.getContent();

        // 1. 일기 조회
        Diary diary = diaryRepo.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일기입니다."));

        // 2. 권한 체크
        boolean owner = forestRepo.findById(forestId)
                .map(f -> f.getUser().getId() == userId)
                .orElse(false);
        boolean shared = sharedForestRepo.existsByUserIdAndForestId(userId, forestId);

        if (!(owner || shared)) {
            throw new AccessDeniedException(
                    String.format("사용자[%d]가 숲[%d]에 대한 쓰기 권한이 없습니다.", userId, forestId)
            );
        }

        // 3. 내용 수정
        diary.setContent(content);

        // 4. 저장
        diaryRepo.save(diary);

        // 5. 결과 반환
        DiaryUpdateResponseDTO res = new DiaryUpdateResponseDTO();
        res.setDiaryId(diary.getId());
        res.setUserId(diary.getUserId());
        res.setForestId(diary.getForestId());
        res.setContent(diary.getContent());
        res.setUpdatedAt(diary.getUpdatedAt());

        return res;
    }
}