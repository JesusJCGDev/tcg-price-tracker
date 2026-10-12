package com.tcg.tracker.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonCardDto(
        String id,
        String name,
        PokemonSetDto set,
        String rarity,
        PokemonImagesDto images,
        PokemonTcgPlayerDto tcgplayer
) {}