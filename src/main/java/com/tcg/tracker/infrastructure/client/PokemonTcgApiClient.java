package com.tcg.tracker.infrastructure.client;

import com.tcg.tracker.infrastructure.client.dto.PokemonCardResponse;
import com.tcg.tracker.infrastructure.client.dto.PokemonSetsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class PokemonTcgApiClient {

    private final RestClient pokemonTcgRestClient;

    public PokemonSetsResponse fetchAllSets() {
        return pokemonTcgRestClient.get()
                .uri("/sets?orderBy=-releaseDate")
                .retrieve()
                .body(PokemonSetsResponse.class);
    }

    public PokemonCardResponse fetchCardsBySet(String setId, int page, int pageSize) {
        return pokemonTcgRestClient.get()
                .uri("/cards?q=set.id:{setId}&page={page}&pageSize={pageSize}",
                        setId, page, pageSize)
                .retrieve()
                .body(PokemonCardResponse.class);
    }
}