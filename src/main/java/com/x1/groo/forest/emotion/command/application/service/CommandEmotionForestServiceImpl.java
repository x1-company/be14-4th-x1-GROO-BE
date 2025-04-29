package com.x1.groo.forest.emotion.command.application.service;

import com.x1.groo.forest.emotion.command.domain.aggregate.*;
import com.x1.groo.forest.emotion.command.domain.repository.*;
import com.x1.groo.forest.emotion.command.domain.vo.RequestMailboxVO;
import com.x1.groo.forest.emotion.command.domain.vo.RequestPlacementVO;
import com.x1.groo.forest.emotion.command.domain.vo.RequestReplacementVO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommandEmotionForestServiceImpl implements CommandEmotionForestService {

    private final PlacementRepository placementRepository;
    private final UserItemRepository userItemRepository;
    private final ForestRepository forestRepository;
    private final UserRepository userRepository;
    private final MailboxRepository mailboxRepository;

    /* 단일 아이템 회수 */
    @Transactional
    @Override
    public void retrieveItemById(int userId, int placementId) {
        PlacementEntity placement = placementRepository.findById(placementId)
                .orElseThrow(() -> new IllegalArgumentException("해당 배치가 존재하지 않습니다. id=" + placementId));

        // userId 검증
        if (placement.getUser().getId() != userId) {
            throw new SecurityException("해당 배치에 접근 권한이 없습니다.");
        }

        UserItemEntity userItem = userItemRepository.findById(placement.getUserItem().getId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저 아이템이 존재하지 않습니다. id=" + placement.getUserItem().getId()));

        userItem.decreasePlacedCount();

        userItemRepository.save(userItem);

        placementRepository.deleteById(placementId);
    }

    /* 전체 아이템 회수 */
    @Transactional
    @Override
    public void retrieveAllItems(int userId, int forestId) {
        // 1. forestId + userId로 user_item 조회
        List<UserItemEntity> userItems = userItemRepository.findByUserIdAndForestId(userId, forestId);

        if (userItems.isEmpty()) {
            return; // 조회된 게 없으면 끝
        }

        // 2. placed_count를 0으로 변경
        for (UserItemEntity userItem : userItems) {
            userItem.setPlacedCount(0);
        }
        userItemRepository.saveAll(userItems);

        // 3. user_item id 목록 가져오기
        List<Integer> userItemIds = userItems.stream()
                .map(UserItemEntity::getId)
                .collect(Collectors.toList());

        // 4. placement 삭제
        placementRepository.deleteByUserItemIdIn(userItemIds);
    }

    /* 아이템 배치 */
    @Transactional
    @Override
    public void placeItem(int userId, RequestPlacementVO requestPlacementVO) {
        
        // 1. userItem 조회 및 검증
        UserItemEntity userItem = userItemRepository.findByIdAndUserId(requestPlacementVO.getUserItemId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("아이템 정보가 일치하지 않습니다"));

        // 2. placed_count 증가
        userItem.setPlacedCount(userItem.getPlacedCount() + 1);

        // 3. userEntity 조회 (PlacementEntity에 필요)
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자가 유효하지 않습니다"));

        // 4. placement 생성 및 저장
        PlacementEntity placement = new PlacementEntity();
        placement.setPositionX(requestPlacementVO.getItemPositionX());
        placement.setPositionY(requestPlacementVO.getItemPositionY());
        placement.setUser(user);
        placement.setUserItem(userItem);

        // 저장
        placementRepository.save(placement);
    }

    /* 아이템 재배치 */
    @Transactional
    @Override
    public void replaceItem(int userId, RequestReplacementVO requestReplacementVO) {
        // 1. placementId로 PlacementEntity 조회
        PlacementEntity placement = placementRepository.findById(requestReplacementVO.getPlacementId())
                .orElseThrow(() -> new IllegalArgumentException("해당 배치가 존재하지 않습니다. id=" + requestReplacementVO.getPlacementId()));

        // 2. 소유자 검증
        if (placement.getUser() == null || placement.getUser().getId() != userId) {
            throw new SecurityException("본인의 배치만 수정할 수 있습니다.");
        }

        // 3. 위치 변경
        placement.setPositionX(requestReplacementVO.getItemPositionX());
        placement.setPositionY(requestReplacementVO.getItemPositionY());

        // 4. 저장 (생략 가능 - JPA의 dirty checking)
    }

    /* 방명록 작성 */
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

    /* 방명록 삭제 */
    @Transactional
    @Override
    public void deleteMailbox(int userId, int mailboxId, int forestId) {
        boolean isOwner = forestRepository.existsByIdAndUserId(forestId, userId);
        if (!isOwner) {
            throw new IllegalArgumentException("본인의 숲에만 방명록을 삭제할 수 있습니다.");
        }

        mailboxRepository.softDeleteById(mailboxId);
    }
}
