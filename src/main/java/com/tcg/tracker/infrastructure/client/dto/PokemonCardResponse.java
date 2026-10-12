package com.tcg.tracker.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonCardResponse(
        List<PokemonCardDto> data,
        int page,
        int pageSize,
        int count,
        int totalCount
) {}