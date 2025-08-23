package com.projeto.vendas.infrastructure.observability;

import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Deve Testar a Classe Performance Service")
class PerformanceServiceTest {

    private MeterRegistry meterRegistry;
    private PerformanceService performanceService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        performanceService = new PerformanceService(meterRegistry);
    }

    @Test
    void deveIniciarEPararTimer() {
        Timer.Sample sample = performanceService.startTimer();

        performanceService.stopTimer(sample, "service_time", "service", "VendaService", "method", "criar");

        Timer timer = meterRegistry.find("service_time")
                .tags("service", "VendaService", "method", "criar")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

    @Test
    void deveIncrementarCounter() {
        performanceService.incrementCounter("service_errors", "service", "VendaService", "method", "criar");

        Counter counter = meterRegistry.find("service_errors")
                .tags("service", "VendaService", "method", "criar")
                .counter();

        assertThat(counter).isNotNull();
        assertThat(counter.count()).isEqualTo(1.0);

        performanceService.incrementCounter("service_errors", "service", "VendaService", "method", "criar");
        assertThat(counter.count()).isEqualTo(2.0);
    }

    @Test
    void deveRegistrarQueryNoBanco() {
        performanceService.recordDatabaseQuery("VendaRepository", "findAll", 150);

        Timer timer = meterRegistry.find("database_query_time")
                .tags("table", "VendaRepository", "operation", "findAll")
                .timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(TimeUnit.MILLISECONDS)).isGreaterThanOrEqualTo(150);
    }

    @Test
    void deveReutilizarMesmosTimersECounters() {
        performanceService.incrementCounter("service_errors", "service", "VendaService", "method", "criar");

        Counter first = meterRegistry.find("service_errors")
                .tags("service", "VendaService", "method", "criar")
                .counter();

        performanceService.incrementCounter("service_errors", "service", "VendaService", "method", "criar");

        Counter second = meterRegistry.find("service_errors")
                .tags("service", "VendaService", "method", "criar")
                .counter();

        assertThat(first).isSameAs(second);
        assertThat(second.count()).isEqualTo(2.0);
    }

    @Test
    void devePararTimerSemTags() {
        Timer.Sample sample = performanceService.startTimer();

        performanceService.stopTimer(sample, "service_time");

        Timer timer = meterRegistry.find("service_time").timer();

        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
    }

}
