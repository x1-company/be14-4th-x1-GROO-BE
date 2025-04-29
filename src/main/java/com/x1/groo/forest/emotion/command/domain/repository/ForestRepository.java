package com.x1.groo.forest.emotion.command.domain.repository;

import com.x1.groo.forest.emotion.command.domain.aggregate.ForestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForestRepository extends JpaRepository<ForestEntity, Integer> {

    boolean existsByIdAndUserId(int id, int userId);
}
