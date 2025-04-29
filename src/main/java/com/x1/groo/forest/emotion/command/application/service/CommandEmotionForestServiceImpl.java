package com.x1.groo.forest.emotion.command.application.service;

import com.x1.groo.forest.emotion.command.domain.aggregate.*;
import com.x1.groo.forest.emotion.command.domain.repository.*;
import com.x1.groo.forest.emotion.command.domain.vo.RequestMailboxVO;
import com.x1.groo.forest.emotion.command.domain.vo.RequestPlacementVO;
import com.x1.groo.forest.emotion.command.domain.vo.RequestReplacementVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandEmotionForestServiceImpl implements CommandEmotionForestService {

    private final PlacementRepository placementRepository;
    private final UserItemRepository userItemRepository;
    private final ForestRepository forestRepository;;
    private final UserRepository userRepository;
    private final MailboxRepository mailboxRepository;

    @Transactional
    @Override
    public void retrieveItemById(int userId, int placementId) {
        PlacementEntity placement = placementRepository.findById(placementId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배치가 존재하지 않습니다. id=" + placementId));

        if (placement.getUser().getId() != userId) {
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }

        UserItemEntity userItem = userItemRepository.findById(placement.getUserItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 아이템이 존재하지 않습니다. id=" + placement.getUserItem().getId()));

        userItem.decreasePlacedCount();

        userItemRepository.save(userItem);

        placementRepository.deleteById(placementId);
    }

    @Transactional
    @Override
    public void retrieveAllItems(int userId, int forestId) {
        // 1. placement 가져오기
        List<PlacementEntity> placements = placementRepository.findByForestIdAndUserId(forestId, userId);

        if (placements.isEmpty()) {
            return; // 삭제할게 없으면 바로 리턴
        }

        // 2. 관련 userItem id 모으기
        List<Integer> userItemIds = placements.stream()
                .map(placement -> placement.getUserItem().getId())
                .toList();

        // 3. userItem 가져오기
        List<UserItemEntity> userItems = userItemRepository.findAllById(userItemIds);

        // 4. id -> entity 매핑
        Map<Integer, UserItemEntity> userItemMap = userItems.stream()
                .collect(Collectors.toMap(UserItemEntity::getId, Function.identity()));

        // 5. placedCount 감소
        for (PlacementEntity placement : placements) {
            UserItemEntity userItem = userItemMap.get(placement.getUserItem().getId());
            if (userItem != null) {
                userItem.decreasePlacedCount();
            }
        }

        // 6. userItem을 db에 반영
        userItemRepository.saveAll(userItemMap.values());

        // 7. placement 삭제
        placementRepository.deleteAll(placements);
    }

    @Transactional
    @Override
    public void placeItem(int userId, RequestPlacementVO requestPlacementVO) {
        // 1. user_item 검증 + placedCount 증가
        UserItemEntity userItem = userItemRepository.findByIdAndUserId(requestPlacementVO.getUserItemId(), userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 소유하고 있지 않습니다."));

        userItem.incrementPlacedCount();  // placedCount += 1

        // 2. forest, user 조회
        ForestEntity forest = forestRepository.findById(requestPlacementVO.getForestId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 숲입니다."));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 3. placement 저장
        PlacementEntity placement = new PlacementEntity(requestPlacementVO.getItemPositionX(),
                                                        requestPlacementVO.getItemPositionY(),
                                                        forest, user, userItem);
        placementRepository.save(placement);
    }

    @Transactional
    @Override
    public void replaceItem(int userId, RequestReplacementVO requestReplacementVO) {
        // 1. placementId로 PlacementEntity 조회
        PlacementEntity placement = placementRepository.findById(requestReplacementVO.getPlacementId())
                .orElseThrow(() -> new IllegalArgumentException("해당 배치가 존재하지 않습니다. id=" + requestReplacementVO.getPlacementId()));

        // 2. 권한 확인
        if (placement.getUser().getId() != userId) {
            throw new IllegalArgumentException("사용자가 일치하지 않습니다.");
        }

        // 3. 위치 변경
        placement.setPositionX(requestReplacementVO.getItemPositionX());
        placement.setPositionY(requestReplacementVO.getItemPositionY());

        // 4. 저장 (save 안 해도 됨 — JPA는 @Transactional 안에서 dirty checking으로 자동 update 됨)
    }

    @Override
    public void createMailbox(int userId, RequestMailboxVO requestMailboxVO) {
        MailboxEntity mailbox = new MailboxEntity();
        mailbox.setContent(requestMailboxVO.getContent());
        mailbox.setUserId(userId);
        mailbox.setForestId(requestMailboxVO.getForestId());
        mailbox.setIsDeleted(false);
        mailbox.setCreatedAt(LocalDateTime.now());

        mailboxRepository.save(mailbox);
    }

    @Transactional
    @Override
    public void deleteMailbox(int userId, int mailboxId, int forestId) {
        boolean isOwner = forestRepository.existsByIdAndUserId(forestId, userId);
        if (!isOwner) {
            throw new IllegalArgumentException("본인의 숲에만 방명록을 삭제할 수 있습니다.");
        }

        mailboxRepository.softDeleteById(mailboxId);
    }

    @Override
    public void updateForestPublic(int forestId, int userId, boolean isPublic) {
        ForestEntity forest = forestRepository.findById(forestId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 숲입니다."));

        // 여기서 꼭 본인 확인
        if (forest.getUser().getId() != userId) {
            throw new AccessDeniedException("본인 소유의 숲만 수정할 수 있습니다.");
        }

        // 본인 소유면 공개 여부 수정
        forest.setPublic(isPublic);
        forestRepository.save(forest);
    }
}
