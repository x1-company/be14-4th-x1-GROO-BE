package com.x1.groo.forest.emotion.command.application.controller;

import com.x1.groo.common.JwtUtil;
import com.x1.groo.forest.emotion.command.application.service.CommandEmotionForestService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emotion-forest")
@Slf4j
public class CommandEmotionForestController {

    private final CommandEmotionForestService commandEmotionForestService;
    private final JwtUtil jwtUtil;

    @Autowired
    public CommandEmotionForestController(CommandEmotionForestService commandEmotionForestService,
                                          JwtUtil jwtUtil) {
        this.commandEmotionForestService = commandEmotionForestService;
        this.jwtUtil = jwtUtil;
    }

    @DeleteMapping("/placement")
    public ResponseEntity<Void> retrieveItemById(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                @RequestParam int placementId) {

        // "Bearer " 부분 제거
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        commandEmotionForestService.retrieveItemById(userId, placementId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/placements")
    public ResponseEntity<Void> retrieveAllItems(@RequestHeader(value = "Authorization") String authorizationHeader,
                                                @RequestParam int forestId) {

        // "Bearer " 부분 제거
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        commandEmotionForestService.retrieveAllItems(userId, forestId);

        return ResponseEntity.ok().build();
    }


}
