package com.tcg.tracker.infrastructure.scheduler;

import com.tcg.tracker.application.ingestion.CardIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CardPriceScheduler {

    private final CardIngestionService ingestionService;

    @Value("${tcg.tracker.all-sets:false}")
    private boolean trackAllSets;

    @Value("${tcg.tracker.monitored-sets:sv3}")
    private List<String> monitoredSets;

    @Scheduled(cron = "${tcg.tracker.cron}")
    public void executeDailyPriceSync() {
        log.info("Iniciando tarea programada de sincronización de precios...");

        if (trackAllSets) {
            ingestionService.ingestAllSets();
        } else {
            for (String setId : monitoredSets) {
                try {
                    int cardsIngested = ingestionService.ingestSet(setId);
                    log.info("Set {} sincronizado con éxito. Registros procesados: {}", setId, cardsIngested);
                } catch (Exception e) {
                    log.error("Error al sincronizar el set {}: {}", setId, e.getMessage());
                }
            }
        }

        log.info("Tarea programada de precios finalizada.");
    }
}
