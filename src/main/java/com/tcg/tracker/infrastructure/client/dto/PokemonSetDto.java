package com.tcg.tracker.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonSetDto(
        String id,
        String name,
        Integer total
) {}