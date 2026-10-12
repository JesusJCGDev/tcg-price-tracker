package com.tcg.tracker.application.ingestion;

import com.tcg.tracker.infrastructure.client.PokemonTcgApiClient;
import com.tcg.tracker.infrastructure.client.dto.PokemonCardDto;
import com.tcg.tracker.infrastructure.client.dto.PokemonCardResponse;
import com.tcg.tracker.infrastructure.client.dto.PokemonSetDto;
import com.tcg.tracker.infrastructure.client.dto.PriceDetailDto;
import com.tcg.tracker.infrastructure.persistence.entity.CardEntity;
import com.tcg.tracker.infrastructure.persistence.entity.PriceSnapshotEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardIngestionServiceTest {

    @Mock
    private PokemonTcgApiClient apiClient;

    @Mock
    private CardSetPersistenceService setPersistenceService;

    @InjectMocks
    private CardIngestionService ingestionService;

    @Captor
    private ArgumentCaptor<List<CardEntity>> cardsCaptor;

    @Captor
    private ArgumentCaptor<List<PriceSnapshotEntity>> snapshotsCaptor;

    @Test
    @DisplayName("Debe descargar todas las páginas en memoria y persistir atómicamente el set completo")
    void shouldFetchAllPagesAndPersistAtomically() {
        String setId = "sv3";

        List<PokemonCardDto> page1Cards = IntStream.rangeClosed(1, 50)
                .mapToObj(i -> createCardDto("sv3-" + i, "Card " + i, new BigDecimal("5.00")))
                .toList();
        List<PokemonCardDto> page2Cards = List.of(createCardDto("sv3-51", "Card 51", new BigDecimal("12.50")));

        PokemonCardResponse page1Response = new PokemonCardResponse(page1Cards, 1, 50, 51, 51);
        PokemonCardResponse page2Response = new PokemonCardResponse(page2Cards, 2, 50, 51, 51);

        when(apiClient.fetchCardsBySet(eq(setId), eq(1), eq(50))).thenReturn(page1Response);
        when(apiClient.fetchCardsBySet(eq(setId), eq(2), eq(50))).thenReturn(page2Response);

        int totalIngested = ingestionService.ingestSet(setId);

        assertEquals(51, totalIngested);

        verify(apiClient, times(2)).fetchCardsBySet(eq(setId), anyInt(), eq(50));
        verify(setPersistenceService, times(1)).saveSetData(cardsCaptor.capture(), snapshotsCaptor.capture());

        List<CardEntity> savedCards = cardsCaptor.getValue();
        List<PriceSnapshotEntity> savedSnapshots = snapshotsCaptor.getValue();

        assertEquals(51, savedCards.size());
        assertEquals("sv3-1", savedCards.getFirst().getId());
        assertEquals("sv3-51", savedCards.get(50).getId());
        assertTrue(savedCards.getFirst().isNew());

        assertEquals(51, savedSnapshots.size());
        assertEquals(new BigDecimal("5.00"), savedSnapshots.getFirst().getPrice());
        assertEquals(new BigDecimal("12.50"), savedSnapshots.get(50).getPrice());
    }

    @Test
    @DisplayName("No debe invocar la capa de persistencia si la respuesta inicial de la API es vacía")
    void shouldNotPersistWhenApiResponseIsEmpty() {
        String setId = "empty-set";
        when(apiClient.fetchCardsBySet(eq(setId), eq(1), eq(50)))
                .thenReturn(new PokemonCardResponse(Collections.emptyList(), 1, 50, 0, 0));

        int result = ingestionService.ingestSet(setId);

        assertEquals(0, result);
        verify(setPersistenceService, never()).saveSetData(cardsCaptor.capture(), snapshotsCaptor.capture());
    }

    private PokemonCardDto createCardDto(String id, String name, BigDecimal price) {
        PriceDetailDto priceDetail = new PriceDetailDto(null, null, null, price, null);
        var tcgPlayer = new com.tcg.tracker.infrastructure.client.dto.PokemonTcgPlayerDto(
                "https://tcgplayer.com/" + id,
                "2026/10/12",
                Map.of("holofoil", priceDetail)
        );

        PokemonSetDto setDto = new PokemonSetDto("sv3", "Obsidian Flames", 230);

        return new PokemonCardDto(
                id,
                name,
                setDto,
                "Rare",
                null,
                tcgPlayer
        );
    }
}
