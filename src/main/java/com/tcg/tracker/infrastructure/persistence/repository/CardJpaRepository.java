package com.tcg.tracker.infrastructure.persistence.repository;

import com.tcg.tracker.infrastructure.persistence.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, String> {

    @Query("SELECT new com.tcg.tracker.infrastructure.persistence.repository.CardCountBySetView(c.setName, COUNT(c)) " +
            "FROM CardEntity c GROUP BY c.setName")
    List<CardCountBySetView> countCardsGroupedBySetName();
}