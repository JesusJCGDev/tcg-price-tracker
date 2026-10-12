package com.tcg.tracker.application.ingestion;

import com.tcg.tracker.infrastructure.client.PokemonTcgApiClient;
import com.tcg.tracker.infrastructure.persistence.repository.CardJpaRepository;
import com.tcg.tracker.infrastructure.persistence.repository.PriceSnapshotJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.tcg.tracker.infrastructure.persistence.repository.CardCountBySetView;
import java.util.Map;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CardIngestionServiceTest {

    @Autowired
    private CardIngestionService ingestionService;

    @Autowired
    private PokemonTcgApiClient apiClient;

    @Autowired
    private CardJpaRepository cardRepository;

    @Autowired
    private PriceSnapshotJpaRepository priceSnapshotRepository;


}