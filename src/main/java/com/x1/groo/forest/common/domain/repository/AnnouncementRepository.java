package com.x1.groo.forest.common.domain.repository;

import com.x1.groo.forest.common.domain.aggregate.AnnouncementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("TempAnnouncementRepository")
public interface AnnouncementRepository extends JpaRepository<AnnouncementEntity, Integer> {
}
