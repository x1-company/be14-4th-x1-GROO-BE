package com.x1.groo.forest.emotion.query.controller;

import com.x1.groo.common.JwtUtil;
import com.x1.groo.forest.emotion.command.application.service.CommandEmotionForestService;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionDetailDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionListDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxListDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import com.x1.groo.forest.emotion.query.service.QueryForestEmotionService;
import io.jsonwebtoken.Claims;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public QueryForestEmotionController(JwtUtil jwtUtil, QueryForestEmotionService queryForestEmotionService) {
        this.jwtUtil = jwtUtil;
        this.queryForestEmotionService = queryForestEmotionService;
    }

    @GetMapping("/items/{categoryId}")
    public ResponseEntity<?> getItems(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @PathVariable int categoryId) {
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        log.info("userId = {}", userId);

        List<QueryForestEmotionUserItemDTO> items = queryForestEmotionService.getPieceOfMemory(userId, categoryId);

        if (items == null || items.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("보유한 기억의 조각이 없습니다. 일기를 써서 더 많은 조각들을 모아봐요🌸");
        }

        return ResponseEntity.ok(items);
    }


    // 감정의 숲에 작성된 방명록 리스트 조회
    @GetMapping("/mailbox-lists/{forestId}")
    public ResponseEntity<List<QueryForestEmotionMailboxListDTO>> getMailboxList(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int forestId) {

        String token = authorizationHeader.replace("Bearer", "").trim();
        int userId = ((Number) jwtUtil.parseJwt(token).get("userId")).intValue();

        List<QueryForestEmotionMailboxListDTO> result = queryForestEmotionService.getMailboxList(userId, forestId);
        return ResponseEntity.ok(result);
    }

    // 감정의 숲에 작성된 방명록 상세 조회
    @GetMapping("/mailbox-detail/{id}")
    public ResponseEntity<List<QueryForestEmotionMailboxDTO>> getMailboxDetail(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int id) {

        String token = authorizationHeader.replace("Bearer", "").trim();
        int userId = ((Number) jwtUtil.parseJwt(token).get("userId")).intValue();

        List<QueryForestEmotionMailboxDTO> result = queryForestEmotionService.getMailboxDetail(userId, id);
        return ResponseEntity.ok(result);
    }

    // 감정의 숲 상세 조회
    @GetMapping("/detail/{forestId}")
    public ResponseEntity<List<QueryForestEmotionDetailDTO>> getForestDetail(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int forestId) {

        String token = authorizationHeader.replace("Bearer", "").trim();
        int userId = ((Number) jwtUtil.parseJwt(token).get("userId")).intValue();

        List<QueryForestEmotionDetailDTO> result = queryForestEmotionService.getForestDetail(userId, forestId);
        return ResponseEntity.ok(result);
    }

    // 소유한 숲 조회
    @GetMapping("/myforest")
    public ResponseEntity<List<QueryForestEmotionListDTO>> getMyForest(
            @RequestHeader(value = "Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer", "").trim();
        int userId = ((Number) jwtUtil.parseJwt(token).get("userId")).intValue();

        List<QueryForestEmotionListDTO> result = queryForestEmotionService.getForestList(userId);
        return ResponseEntity.ok(result);
    }
}
