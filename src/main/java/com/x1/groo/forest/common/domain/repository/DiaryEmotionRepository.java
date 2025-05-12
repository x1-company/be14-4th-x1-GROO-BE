package com.x1.groo.forest.common.domain.repository;

import com.x1.groo.forest.common.domain.aggregate.DiaryEmotionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("TempDiaryEmotionRepository")
public interface DiaryEmotionRepository extends JpaRepository<DiaryEmotionEntity, Integer> {
}
