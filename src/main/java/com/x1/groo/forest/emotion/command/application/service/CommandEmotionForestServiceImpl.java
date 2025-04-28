package com.x1.groo.forest.emotion.command.application.service;

import com.x1.groo.forest.emotion.command.domain.aggregate.PlacementEntity;
import com.x1.groo.forest.emotion.command.domain.aggregate.UserItemEntity;
import com.x1.groo.forest.emotion.command.domain.repository.PlacementRepository;
import com.x1.groo.forest.emotion.command.domain.repository.UserItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommandEmotionForestServiceImpl implements CommandEmotionForestService {

    private final PlacementRepository placementRepository;
    private final UserItemRepository userItemRepository;

    @Autowired
    public CommandEmotionForestServiceImpl(PlacementRepository placementRepository, UserItemRepository userItemRepository) {
        this.placementRepository = placementRepository;
        this.userItemRepository = userItemRepository;
    }

    @Transactional
    @Override
    public void unplaceItemById(int userId, int placementId) {
        PlacementEntity placement = placementRepository.findById(placementId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배치가 존재하지 않습니다. id=" + placementId));

        if (placement.getUserId() != userId) {
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }

        UserItemEntity userItem = userItemRepository.findById(placement.getUserItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 아이템이 존재하지 않습니다. id=" + placement.getUserItemId()));

        userItem.decreasePlacedCount();

        userItemRepository.save(userItem);

        placementRepository.deleteById(placementId);
    }

    @Transactional
    @Override
    public void unplaceAllItems(int userId, int forestId) {
        // 1. placement 가져오기
        List<PlacementEntity> placements = placementRepository.findByForestIdAndUserId(forestId, userId);

        if (placements.isEmpty()) {
            return; // 삭제할게 없으면 바로 리턴
        }

        // 2. 관련 userItem id 모으기
        List<Integer> userItemIds = placements.stream()
                .map(PlacementEntity::getUserItemId)
                .toList();

        // 3. userItem 가져오기
        List<UserItemEntity> userItems = userItemRepository.findAllById(userItemIds);

        // 4. id -> entity 매핑
        Map<Integer, UserItemEntity> userItemMap = userItems.stream()
                .collect(Collectors.toMap(UserItemEntity::getId, Function.identity()));

        // 5. placedCount 감소
        for (PlacementEntity placement : placements) {
            UserItemEntity userItem = userItemMap.get(placement.getUserItemId());
            if (userItem != null) {
                userItem.decreasePlacedCount();
            }
        }

        // 6. userItem을 db에 반영
        userItemRepository.saveAll(userItemMap.values());

        // 7. placement 삭제
        placementRepository.deleteAll(placements);
    }
}
