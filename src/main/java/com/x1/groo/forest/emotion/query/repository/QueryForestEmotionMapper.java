package com.x1.groo.forest.emotion.query.repository;

import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionDetailDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionMailboxListDTO;
import com.x1.groo.forest.emotion.query.dto.QueryForestEmotionUserItemDTO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QueryForestEmotionMapper {

    // 사용자가 보유한 기억의 조각 카테고리별 조회
    List<QueryForestEmotionUserItemDTO> findPieceOfMemory(
            @Param("userId") int userId,
            @Param("categoryId") int categoryId
            );

    List<QueryForestEmotionMailboxListDTO> findMailboxList(
            @Param("forestId") int forestId
            );

    List<QueryForestEmotionMailboxDTO> findMailboxDetail(
            @Param("id") int id
    );

    List<QueryForestEmotionDetailDTO> findForestDetail(
            @Param("forestId") int forestId
    );
}
