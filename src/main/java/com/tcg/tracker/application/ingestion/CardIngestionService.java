package com.tcg.tracker.application.ingestion;

import com.tcg.tracker.infrastructure.client.PokemonTcgApiClient;
import com.tcg.tracker.infrastructure.client.dto.PokemonCardDto;
import com.tcg.tracker.infrastructure.client.dto.PokemonCardResponse;
import com.tcg.tracker.infrastructure.client.dto.PokemonSetDto;
import com.tcg.tracker.infrastructure.client.dto.PriceDetailDto;
import com.tcg.tracker.infrastructure.persistence.entity.CardEntity;
import com.tcg.tracker.infrastructure.persistence.entity.PriceSnapshotEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardIngestionService {

    private final PokemonTcgApiClient apiClient;
    private final CardSetPersistenceService setPersistenceService;

    public int ingestSet(String setId) {
        log.info("Iniciando ingesta de cartas para el set: {}", setId);
        int page = 1;
        int pageSize = 50;
        boolean hasMore = true;
        List<PokemonCardDto> fetchedCards = new ArrayList<>();

        while (hasMore) {
            PokemonCardResponse response = apiClient.fetchCardsBySet(setId, page, pageSize);

            if (response == null || response.data() == null || response.data().isEmpty()) {
                break;
            }

            fetchedCards.addAll(response.data());
            log.info("Descargadas {} cartas del set {} (Página {})", fetchedCards.size(), setId, page);

            if (fetchedCards.size() >= response.totalCount() || response.data().size() < pageSize) {
                hasMore = false;
            } else {
                page++;
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        if (fetchedCards.isEmpty()) {
            log.warn("No se encontraron cartas para el set: {}", setId);
            return 0;
        }

        Instant now = Instant.now();
        List<CardEntity> cardsToSave = new ArrayList<>(fetchedCards.size());
        List<PriceSnapshotEntity> snapshotsToSave = new ArrayList<>(fetchedCards.size());

        for (PokemonCardDto dto : fetchedCards) {
            CardEntity cardEntity = CardEntity.builder()
                    .id(dto.id())
                    .name(dto.name())
                    .setName(dto.set() != null ? dto.set().name() : setId)
                    .rarity(dto.rarity())
                    .imageUrl(dto.images() != null ? dto.images().large() : null)
                    .createdAt(now)
                    .isNew(true)
                    .build();

            cardsToSave.add(cardEntity);

            BigDecimal marketPrice = extractMarketPrice(dto);
            if (marketPrice != null) {
                PriceSnapshotEntity snapshot = PriceSnapshotEntity.builder()
                        .card(cardEntity)
                        .marketplace("TCGPLAYER")
                        .price(marketPrice)
                        .recordedAt(now)
                        .build();
                snapshotsToSave.add(snapshot);
            }
        }

        setPersistenceService.saveSetData(cardsToSave, snapshotsToSave);

        log.info("Ingesta finalizada para el set {}. Total cartas registradas: {}", setId, cardsToSave.size());
        return cardsToSave.size();
    }

    private BigDecimal extractMarketPrice(PokemonCardDto dto) {
        if (dto.tcgplayer() == null || dto.tcgplayer().prices() == null) {
            return null;
        }

        var prices = dto.tcgplayer().prices();
        PriceDetailDto detail = prices.getOrDefault("normal",
                prices.getOrDefault("holofoil",
                        prices.getOrDefault("reverseHolofoil", null)));

        if (detail != null && detail.market() != null) {
            return detail.market();
        }

        return prices.values().stream()
                .map(PriceDetailDto::market)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public void ingestAllSets() {
        log.info("Obteniendo catálogo completo de sets desde la API...");
        var setsResponse = apiClient.fetchAllSets();
        if (setsResponse == null || setsResponse.data() == null || setsResponse.data().isEmpty()) {
            log.warn("No se pudieron recuperar los sets de la API.");
            return;
        }

        List<String> setIds = setsResponse.data().stream()
                .map(PokemonSetDto::id)
                .toList();

        log.info("Se encontraron {} sets en total para sincronizar.", setIds.size());

        int grandTotal = 0;
        int current = 1;
        for (String setId : setIds) {
            try {
                log.info("[{}/{}] Sincronizando set: {}", current, setIds.size(), setId);
                int ingested = ingestSet(setId);
                grandTotal += ingested;
                Thread.sleep(1500);
            } catch (Exception e) {
                log.error("Error al procesar el set {}: {}", setId, e.getMessage());
            }
            current++;
        }

        log.info("Sincronización global de todos los sets completada. Total acumulado: {} cartas.", grandTotal);
    }
}
