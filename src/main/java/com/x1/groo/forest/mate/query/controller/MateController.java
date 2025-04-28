package com.x1.groo.forest.mate.query.controller;

import com.x1.groo.common.JwtUtil;
import com.x1.groo.forest.mate.query.dto.DiaryByDateDTO;
import com.x1.groo.forest.mate.query.service.MateServiceImpl;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/mate")
@RequiredArgsConstructor
public class MateController {

    private final MateServiceImpl mateService;
    private final JwtUtil jwtUtil;   // JwtUtil 주입 추가

    @GetMapping("/diary/{forestId}/date")
    public ResponseEntity<List<DiaryByDateDTO>> getDiariesByDate(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @PathVariable int forestId,
            @RequestParam LocalDate date
    ) {
        // JWT에서 userId 추출
        String token = authorizationHeader.replace("Bearer", "").trim();
        Claims claims = jwtUtil.parseJwt(token);
        int userId = ((Number) claims.get("userId")).intValue();

        // 서비스 호출
        List<DiaryByDateDTO> diaries = mateService.findDiaries(userId, forestId, date);
        return ResponseEntity.ok(diaries);
    }
}
