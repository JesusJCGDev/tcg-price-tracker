package com.tcg.tracker.infrastructure.persistence.repository;

import com.tcg.tracker.infrastructure.persistence.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface CardJpaRepository extends JpaRepository<CardEntity, String> {

    @Query("SELECT c.id FROM CardEntity c WHERE c.id IN :ids")
    Set<String> findExistingCardIds(@Param("ids") Collection<String> ids);
}
