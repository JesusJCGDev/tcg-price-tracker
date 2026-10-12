package com.tcg.tracker.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokemonTcgPlayerDto(
        String url,
        String updatedAt,
        Map<String, PriceDetailDto> prices
) {}