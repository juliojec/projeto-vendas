package com.projeto.vendas.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Deve Testar o Enum Status Venda")
class StatusVendaTest {

    @Test
    void deveConterTodosOsValores() {
        StatusVenda[] valores = StatusVenda.values();
        assertThat(valores)
                .containsExactlyInAnyOrder(StatusVenda.ATIVA, StatusVenda.CANCELADA);
    }

    @Test
    void getDescricao_deveRetornarDescricaoCorreta() {
        assertEquals("Ativa", StatusVenda.ATIVA.getDescricao());
        assertEquals("Cancelada", StatusVenda.CANCELADA.getDescricao());
    }

    @Test
    void toString_deveRetornarDescricaoCorreta() {
        assertEquals("Ativa", StatusVenda.ATIVA.toString());
        assertEquals("Cancelada", StatusVenda.CANCELADA.toString());
    }

    @Test
    void valueOf_deveFuncionarCorretamente() {
        assertEquals(StatusVenda.ATIVA, StatusVenda.valueOf("ATIVA"));
        assertEquals(StatusVenda.CANCELADA, StatusVenda.valueOf("CANCELADA"));
    }
}
