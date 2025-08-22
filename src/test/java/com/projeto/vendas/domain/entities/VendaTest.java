package com.projeto.vendas.domain.entities;

import com.projeto.vendas.domain.enums.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Deve Testar a Entidade Venda")
class VendaTest {

    private Venda venda;
    private ItemVenda item1;
    private ItemVenda item2;

    @BeforeEach
    void setUp() {
        venda = new Venda("VD-001", "123", "João", "F01", "Filial 1");

        item1 = new ItemVenda("P001", "Produto 1", 2, new BigDecimal("10.0"));
        item2 = new ItemVenda("P002", "Produto 2", 1, new BigDecimal("20.0"));
    }

    @Test
    void deveAdicionarItens() {
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);

        assertEquals(2, venda.getItens().size());
        assertEquals(venda, item1.getVenda());
        assertEquals(venda, item2.getVenda());
    }

    @Test
    void naoDeveAdicionarItemSeVendaCancelada() {
        venda.cancelar();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> venda.adicionarItem(item1));
        assertEquals("Não é possível alterar venda cancelada", ex.getMessage());
    }

    @Test
    void deveCancelarVenda() {
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);

        venda.cancelar();

        assertTrue(venda.getCancelada());
        assertTrue(venda.getItens().stream().allMatch(ItemVenda::getCancelado));
    }

    @Test
    void naoDeveCancelarVendaJaCancelada() {
        venda.cancelar();
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> venda.cancelar());
        assertEquals("Venda já está cancelada", ex.getMessage());
    }

    @Test
    void deveRetornarStatusCorreto() {
        assertEquals(StatusVenda.ATIVA, venda.getStatus());
        venda.cancelar();
        assertEquals(StatusVenda.CANCELADA, venda.getStatus());
    }

}
