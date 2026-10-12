package com.tcg.tracker.infrastructure.persistence.repository;

public record CardCountBySetView(
        String setName,
        long count
) {}