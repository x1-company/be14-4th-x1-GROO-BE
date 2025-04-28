package com.x1.groo.forest.emotion.query.controller;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import com.x1.groo.forest.emotion.query.service.QueryForestEmotionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
public class QueryForestEmotionController {

    @Autowired
    private QueryForestEmotionService queryForestEmotionService;

    // 사용자가 보유한 기억의 조각 카테고리별 조회
    @GetMapping("/{userId}/{category}")
    public List<QueryForestEmotionUserItemDTO> getItems(
            @PathVariable int userId,
            @PathVariable int categoryId) {
        return queryForestEmotionService.getPieceOfMemory(userId, categoryId);
    }
}
