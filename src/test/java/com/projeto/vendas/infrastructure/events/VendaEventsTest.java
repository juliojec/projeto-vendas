package com.projeto.vendas.infrastructure.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Deve Testar a Classe de Eventos de Vendas")
class VendaEventsTest {

    @Test
    void deveCriarCompraEfetuada() {
        VendaEvents.CompraEfetuada evento = new VendaEvents.CompraEfetuada(
                "VD-123",
                "C001",
                "João Silva",
                BigDecimal.valueOf(150.75),
                3);

        assertThat(evento.getNumeroVenda()).isEqualTo("VD-123");
        assertThat(evento.getClienteNome()).isEqualTo("João Silva");
        assertThat(evento.getValorTotal()).isEqualByComparingTo("150.75");
        assertThat(evento.getTotalItens()).isEqualTo(3);
        assertThat(evento.getTimestamp()).isNotNull();
    }

    @Test
    void deveCriarCompraAlterada() {
        VendaEvents.CompraAlterada evento = new VendaEvents.CompraAlterada(
                "VD-456",
                "ALTERAÇÃO DE ITENS",
                "Adicionado produto novo");

        assertThat(evento.getNumeroVenda()).isEqualTo("VD-456");
        assertThat(evento.getTipoAlteracao()).contains("ALTERAÇÃO");
        assertThat(evento.getDetalhes()).isEqualTo("Adicionado produto novo");
        assertThat(evento.getTimestamp()).isNotNull();
    }

    @Test
    void deveCriarCompraCancelada() {
        VendaEvents.CompraCancelada evento = new VendaEvents.CompraCancelada(
                "VD-789",
                "C002",
                "Maria Oliveira",
                BigDecimal.valueOf(200.00),
                "Cliente desistiu");

        assertThat(evento.getClienteNome()).isEqualTo("Maria Oliveira");
        assertThat(evento.getValorCancelado()).isEqualByComparingTo("200.00");
        assertThat(evento.getMotivo()).isEqualTo("Cliente desistiu");
        assertThat(evento.getTimestamp()).isNotNull();
    }

    @Test
    void deveCriarItemCancelado() {
        VendaEvents.ItemCancelado evento = new VendaEvents.ItemCancelado(
                "VD-999",
                10L,
                "P123",
                "Notebook Dell",
                1,
                BigDecimal.valueOf(3500.00)
        );

        assertThat(evento.getNumeroVenda()).isEqualTo("VD-999");
        assertThat(evento.getProdutoDescricao()).isEqualTo("Notebook Dell");
        assertThat(evento.getQuantidade()).isEqualTo(1);
        assertThat(evento.getValorCancelado()).isEqualByComparingTo("3500.00");
        assertThat(evento.getTimestamp()).isNotNull();
    }
}
