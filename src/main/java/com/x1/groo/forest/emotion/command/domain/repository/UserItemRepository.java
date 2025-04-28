package com.x1.groo.forest.emotion.command.domain.repository;

import com.x1.groo.forest.emotion.command.domain.aggregate.UserItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserItemRepository extends JpaRepository<UserItemEntity, Integer> {
}
