package com.x1.groo.forest.emotion.command.application.service;

import com.x1.groo.forest.emotion.command.domain.aggregate.PlacementEntity;
import com.x1.groo.forest.emotion.command.domain.aggregate.UserItemEntity;
import com.x1.groo.forest.emotion.command.domain.repository.PlacementRepository;
import com.x1.groo.forest.emotion.command.domain.repository.UserItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
