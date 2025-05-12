package com.x1.groo.forest.common.domain.repository;

import com.x1.groo.forest.common.domain.aggregate.DiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<DiaryEntity, Integer> {
}
