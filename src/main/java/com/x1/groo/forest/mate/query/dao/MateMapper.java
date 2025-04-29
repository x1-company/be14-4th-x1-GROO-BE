package com.x1.groo.forest.mate.query.dao;

import com.x1.groo.forest.mate.query.dto.DiaryByDateDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MateMapper {

    List<DiaryByDateDTO> findDiaryByDateAndForestId(
            @Param("forestId") int forestId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    // userId가 forestId에 있는지 확인
    boolean existsUserInForest(@Param("userId") int userId, @Param("forestId") int forestId);
}


