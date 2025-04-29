package com.x1.groo.forest.mate.command.domain.repository;

import com.x1.groo.forest.mate.command.domain.aggregate.MateForestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("MateForestRepository")
public interface ForestRepository extends JpaRepository<MateForestEntity, Integer> {
}
