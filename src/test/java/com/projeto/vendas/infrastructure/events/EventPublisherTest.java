package com.projeto.vendas.infrastructure.events;

import com.projeto.vendas.infrastructure.observability.PerformanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@DisplayName("Deve Testar a Classe Event Publisher")
class EventPublisherTest {

    private PerformanceService metricsService;
    private EventPublisher publisher;

    @BeforeEach
    void setUp() {
        metricsService = Mockito.mock(PerformanceService.class);
        publisher = new EventPublisher(metricsService);
    }

    @Test
    void devePublicarCompraEfetuada() {
        var evento = new VendaEvents.CompraEfetuada(
                "VD-123", "C001", "João",
                BigDecimal.valueOf(150), 3);

        publisher.publish(evento);

        verify(metricsService).incrementCounter("vendas_eventos_total", "evento", "criada");
    }

    @Test
    void devePublicarCompraCancelada() {
        var evento = new VendaEvents.CompraCancelada(
                "VD-456", "C002", "Maria",
                BigDecimal.valueOf(200), "Cliente desistiu"
        );

        publisher.publish(evento);

        verify(metricsService).incrementCounter("vendas_eventos_total", "evento", "cancelada");
    }

    @Test
    void devePublicarCompraAlterada() {
        var evento = new VendaEvents.CompraAlterada(
                "VD-789", "ALTERAÇÃO", "Detalhes da alteração");

        publisher.publish(evento);

        verify(metricsService, never()).incrementCounter(anyString(), any(), any());
    }

    @Test
    void devePublicarItemCancelado() {
        var evento = new VendaEvents.ItemCancelado(
                "VD-999", 10L, "P123", "Notebook Dell",
                1, BigDecimal.valueOf(3500));

        publisher.publish(evento);
        verify(metricsService, never()).incrementCounter(anyString(), any(), any());
    }

    @Test
    void devePublicarEventoGenerico() {
        record OutroEvento(String dado) {}
        var evento = new OutroEvento("teste");

        publisher.publish(evento);

        verify(metricsService, never()).incrementCounter(anyString(), any(), any());
    }

    @Test
    void deveTratarErroAoPublicar() {
        Object evento = Mockito.mock(Object.class);

        when(evento.toString()).thenThrow(new RuntimeException("Falha simulada"));

        publisher.publish(evento);

        verifyNoInteractions(metricsService);
    }
}
