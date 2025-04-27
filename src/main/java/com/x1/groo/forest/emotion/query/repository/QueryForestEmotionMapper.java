package com.x1.groo.forest.emotion.query.repository;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QueryForestEmotionMapper {

    // 사용자가 보유한 기억의 조각 카테고리별 조회
    List<QueryForestEmotionUserItemDTO> findPieceOfMemory(
            @Param("userId") int id,
            @Param("category") int category
            );
}
