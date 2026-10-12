package com.tcg.tracker.application.ingestion;

import com.tcg.tracker.infrastructure.persistence.entity.CardEntity;
import com.tcg.tracker.infrastructure.persistence.entity.PriceSnapshotEntity;
import com.tcg.tracker.infrastructure.persistence.repository.CardJpaRepository;
import com.tcg.tracker.infrastructure.persistence.repository.PriceSnapshotJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSetPersistenceService {

    private final CardJpaRepository cardRepository;
    private final PriceSnapshotJpaRepository priceSnapshotRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSetData(List<CardEntity> cards, List<PriceSnapshotEntity> snapshots) {
        if (!cards.isEmpty()) {
            List<String> cardIds = cards.stream()
                    .map(CardEntity::getId)
                    .toList();

            Set<String> existingIds = cardRepository.findExistingCardIds(cardIds);

            List<CardEntity> newCards = cards.stream()
                    .filter(c -> !existingIds.contains(c.getId()))
                    .toList();

            if (!newCards.isEmpty()) {
                cardRepository.saveAll(newCards);
                log.info("Persistidas {} nuevas cartas en el catálogo.", newCards.size());
            }
        }

        if (!snapshots.isEmpty()) {
            priceSnapshotRepository.saveAll(snapshots);
            log.info("Persistidos {} snapshots de precios.", snapshots.size());
        }
    }
}
