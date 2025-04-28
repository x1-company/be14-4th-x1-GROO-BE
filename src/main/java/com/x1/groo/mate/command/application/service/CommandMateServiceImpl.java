package com.x1.groo.mate.command.application.service;

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
}
