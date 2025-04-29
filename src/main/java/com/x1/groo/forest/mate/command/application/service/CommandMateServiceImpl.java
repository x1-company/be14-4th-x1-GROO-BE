package com.x1.groo.forest.mate.command.application.service;

import com.x1.groo.forest.mate.command.domain.aggregate.SharedForestEntity;
import com.x1.groo.forest.mate.command.domain.repository.SharedForestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommandMateServiceImpl implements CommandMateService {

    // 문자열(String)로 key-value를 저장하는 간단한 템플릿
    private final StringRedisTemplate redisTemplate;
    private final SharedForestRepository sharedForestRepository;

    @Override
    public String createInviteLink(int forestId) {


        // 초대 링크 생성 로직 작성
        // 예: UUID 기반 inviteCode 생성
        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        // Redis에 저장
        String redisKey = "invite:" + inviteCode;
        String redisValue = String.valueOf(forestId);

        redisTemplate.opsForValue()
                .set(redisKey, redisValue, Duration.ofHours(24));

        return inviteCode;
    }

    // 초대 수락
    @Transactional
    @Override
    public void acceptInvite(int userId, String inviteCode) {

        String redisKey = "invite:" + inviteCode;
        String value = redisTemplate.opsForValue().get(redisKey);

        if (value == null) {
            throw new IllegalArgumentException("초대코드가 만료되었거나 유효하지 않습니다.");
        }

        int forestId;
        try {
            forestId = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("초대 코드에 해당하는 데이터가 잘못되었습니다.");
        }

        // 1. 이미 수락했는지 검사
        boolean alreadyJoined = sharedForestRepository.existsByUserIdAndForestId(userId, forestId);
        if (alreadyJoined) {
            throw new IllegalStateException("이미 이 숲에 참여한 사용자입니다.");
        }

        // 2. 현재 공유숲 참여 인원이 4명 이상인지 검사
        int currentMemberCount = sharedForestRepository.countByForestId(forestId);
        if (currentMemberCount >= 4) {
            throw new IllegalStateException("이 공유숲은 정원이 가득 찼습니다.");
        }

        // 3. 가입
        SharedForestEntity sharedForest = new SharedForestEntity(userId, forestId);
        sharedForestRepository.save(sharedForest);

        // 4. 초대코드 삭제
        redisTemplate.delete(redisKey);

    }
}
