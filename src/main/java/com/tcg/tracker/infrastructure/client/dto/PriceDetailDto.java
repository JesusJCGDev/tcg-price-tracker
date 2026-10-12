package com.tcg.tracker.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PriceDetailDto(
        BigDecimal low,
        BigDecimal mid,
        BigDecimal high,
        BigDecimal market,
        BigDecimal directLow
) {}