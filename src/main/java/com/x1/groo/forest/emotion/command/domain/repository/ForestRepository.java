package com.x1.groo.forest.emotion.command.domain.repository;

import com.x1.groo.forest.emotion.command.domain.aggregate.ForestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ForestRepository extends JpaRepository<ForestEntity, Integer> {

    boolean existsByIdAndUserId(int id, int userId);

    // 공유된 숲에 userId가 속해 있는지 여부 확인
    @Query("SELECT COUNT(sf) > 0 FROM SharedForestEntity sf WHERE sf.userId = :userId AND sf.forestId = :forestId")
    boolean isUserInSharedForest(@Param("userId") int userId, @Param("forestId") int forestId);
}
