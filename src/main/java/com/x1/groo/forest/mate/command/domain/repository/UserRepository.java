package com.x1.groo.forest.mate.command.domain.repository;


import com.x1.groo.forest.mate.command.domain.aggregate.MateUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<MateUserEntity, Integer> {
}
