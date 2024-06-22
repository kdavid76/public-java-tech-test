package com.global.aod.interview.techtest.repository;

import com.global.aod.interview.techtest.model.entity.StationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<StationEntity, Long> {
}
