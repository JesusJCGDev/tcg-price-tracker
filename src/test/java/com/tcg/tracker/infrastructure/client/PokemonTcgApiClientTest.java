package com.tcg.tracker.infrastructure.client;

import com.tcg.tracker.infrastructure.client.dto.PokemonCardResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class PokemonTcgApiClientTest {

    @Autowired
    private PokemonTcgApiClient apiClient;

    @Test
    @DisplayName("Debe consultar y deserializar cartas del set sv03 (Obsidian Flames)")
    void shouldFetchCardsSuccessfully() {
        String setId = "sv3";
        int page = 1;
        int pageSize = 5;

        PokemonCardResponse response = apiClient.fetchCardsBySet(setId, page, pageSize);

        assertNotNull(response, "La respuesta no debe ser nula");
        assertNotNull(response.data(), "La lista de datos no debe ser nula");
        assertFalse(response.data().isEmpty(), "Debe devolver al menos una carta");
        assertEquals(5, response.data().size(), "El tamaño de la página solicitado debe coincidir");

        var firstCard = response.data().getFirst();
        assertNotNull(firstCard.id(), "El ID de la carta no debe ser nulo");
        assertNotNull(firstCard.name(), "El nombre de la carta no debe ser nulo");
        assertNotNull(firstCard.images(), "El objeto de imágenes no debe ser nulo");
        assertNotNull(firstCard.images().small(), "La URL de la imagen pequeña no debe ser nula");

        log.info("Test de API exitoso para el set: {}", setId);
        log.info("Total de cartas reportadas en el set: {}", response.totalCount());
        log.info("Primera carta obtenida: {} ({})", firstCard.name(), firstCard.id());
        if (firstCard.tcgplayer() != null && firstCard.tcgplayer().prices() != null) {
            log.info("Precios TCGPlayer encontrados: {}", firstCard.tcgplayer().prices().keySet());
        }
    }
}
