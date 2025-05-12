package com.x1.groo.forest.common.domain.repository;

import com.x1.groo.forest.common.domain.aggregate.SharedForestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedForestRepository extends JpaRepository<SharedForestEntity, Integer> {
    boolean existsByUserIdAndForestId(int userId, int forestId);

    int countByForestId(int forestId);

    void deleteByUserIdAndForestId(int userId, int id);

}
