package com.tcg.tracker.infrastructure.persistence.repository;

import com.tcg.tracker.infrastructure.persistence.entity.PriceSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceSnapshotJpaRepository extends JpaRepository<PriceSnapshotEntity, Long> {
    List<PriceSnapshotEntity> findByCardIdOrderByRecordedAtDesc(String cardId);
}