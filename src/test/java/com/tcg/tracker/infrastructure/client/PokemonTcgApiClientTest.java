package com.tcg.tracker.infrastructure.client;

import com.tcg.tracker.infrastructure.client.dto.PokemonCardResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PokemonTcgApiClientTest {

    @Autowired
    private PokemonTcgApiClient apiClient;

    @Test
    @DisplayName("Debe consultar y deserializar cartas del set sv03 (Obsidian Flames)")
    void shouldFetchCardsSuccessfully() {
        // Solicitamos una página pequeña (5 cartas) del set sv03
        String setId = "sv3";
        int page = 1;
        int pageSize = 5;

        PokemonCardResponse response = apiClient.fetchCardsBySet(setId, page, pageSize);

        // Verificaciones del contenedor general
        assertNotNull(response, "La respuesta no debe ser nula");
        assertNotNull(response.data(), "La lista de datos no debe ser nula");
        assertFalse(response.data().isEmpty(), "Debe devolver al menos una carta");
        assertEquals(5, response.data().size(), "El tamaño de la página solicitado debe coincidir");

        // Verificaciones de una carta concreta
        var firstCard = response.data().get(0);
        assertNotNull(firstCard.id(), "El ID de la carta no debe ser nulo");
        assertNotNull(firstCard.name(), "El nombre de la carta no debe ser nulo");
        assertNotNull(firstCard.images(), "El objeto de imágenes no debe ser nulo");
        assertNotNull(firstCard.images().small(), "La URL de la imagen pequeña no debe ser nula");

        // Logging en consola para inspección manual
        System.out.println("============================================");
        System.out.println("Test de API exitoso.");
        System.out.println("Set consultado: " + setId);
        System.out.println("Total de cartas en el set: " + response.totalCount());
        System.out.println("Primera carta obtenida: " + firstCard.name() + " (" + firstCard.id() + ")");
        if (firstCard.tcgplayer() != null && firstCard.tcgplayer().prices() != null) {
            System.out.println("Precios TCGPlayer encontrados: " + firstCard.tcgplayer().prices().keySet());
        }
        System.out.println("============================================");
    }
}