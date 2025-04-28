package com.x1.groo.forest.mate.command.application.controller;

import com.x1.groo.common.JwtUtil;
import com.x1.groo.forest.mate.command.application.service.CommandMateService;
import com.x1.groo.forest.mate.command.domain.vo.CreateInviteRequest;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mate")
public class CommandMateController {

    private final JwtUtil jwtUtil;
    private final CommandMateService commandMateService;

    @Autowired
    public CommandMateController(JwtUtil jwtUtil, CommandMateService commandMateService) {
        this.jwtUtil = jwtUtil;
        this.commandMateService = commandMateService;
    }

    // 초대 링크 생성
    @PostMapping("/create")
    public CreateInviteRequest createInviteLink(@RequestParam int forestId) {

        // 초대 링크 생성하고 결과 받기
        String inviteCode = commandMateService.createInviteLink(forestId);

        // 초대코드를 이용해서 최종 초대링크로 감싸기
        String inviteLink = "https://localhost:8080/mate/invite/" + inviteCode;
        return new CreateInviteRequest(inviteLink);
    }

    // 초대 수락
    @PostMapping("/accept/{inviteCode}")
    public ResponseEntity<String> acceptInvite (@RequestHeader(value = "Authorization") String authorizationHeader,
                                                @PathVariable String inviteCode) {
        // "Bearer " 부분 제거
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        commandMateService.acceptInvite(userId, inviteCode);

        return ResponseEntity.ok("초대 수락 성공");


    }



}
