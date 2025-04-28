package com.x1.groo.forest.emotion.query.controller;

import com.x1.groo.common.JwtUtil;
import com.x1.groo.forest.emotion.command.application.service.CommandEmotionForestService;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import com.x1.groo.forest.emotion.query.service.QueryForestEmotionService;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class QueryForestEmotionController {

    private final JwtUtil jwtUtil;
    private final QueryForestEmotionService queryForestEmotionService;

    @Autowired
    public QueryForestEmotionController(JwtUtil jwtUtil, QueryForestEmotionService queryForestEmotionService,
                                        CommandEmotionForestService commandEmotionForestService) {
        this.jwtUtil = jwtUtil;
        this.queryForestEmotionService = queryForestEmotionService;
    }

    // 사용자가 보유한 기억의 조각 카테고리별 조회
    @GetMapping("/items/{categoryId}")
    public ResponseEntity<List<QueryForestEmotionUserItemDTO>> getItems(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int categoryId) {
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        log.info("userId = {}", userId);

        List<QueryForestEmotionUserItemDTO> items = queryForestEmotionService.getPieceOfMemory(userId, categoryId);

        return ResponseEntity.ok(items);
    }

    // 감정의 숲에 작성된 방명록 리스트 조회
    @GetMapping("/mailbox-lists/{forestId}")
    public ResponseEntity<List<QueryForestEmotionMailboxDTO>> getMailboxList(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int forestId) {
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        log.info("userId = {}", userId);

        List<QueryForestEmotionMailboxDTO> mailboxList = queryForestEmotionService.getMailboxList(userId, forestId);

        return ResponseEntity.ok(mailboxList);
    }


    @GetMapping("/mailbox-detail/{id}")
    public ResponseEntity<List<QueryForestEmotionMailboxDTO>> getMailboxDetail (
            @RequestHeader (value = "Authorization") String authorizationHeader,
            @PathVariable int id) {
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        log.info("userId = {}", userId);

        List<QueryForestEmotionMailboxDTO> mailboxDetail = queryForestEmotionService.getMailboxDetail(userId, id);
        return ResponseEntity.ok(mailboxDetail);
    }

}
