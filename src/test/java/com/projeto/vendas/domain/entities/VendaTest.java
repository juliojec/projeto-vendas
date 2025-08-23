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
        item1.setId(1L);
        item2 = new ItemVenda("P002", "Produto 2", 1, new BigDecimal("20.0"));
        item2.setId(2L);
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

    @Test
    @DisplayName("Deve cancelar um item existente")
    void deveCancelarItemExistente() {
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);

        venda.cancelarItem(item1.getId());

        assertTrue(item1.getCancelado(), "O item 1 deve estar cancelado");
        assertFalse(item2.getCancelado(), "O item 2 não deve ser cancelado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cancelar item inexistente")
    void deveLancarExcecaoItemInexistente() {
        venda.adicionarItem(item1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> venda.cancelarItem(999L));

        assertEquals("Item não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Não deve cancelar item se a venda já estiver cancelada")
    void naoDeveCancelarItemSeVendaCancelada() {
        venda.adicionarItem(item1);
        venda.cancelar();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> venda.cancelarItem(item1.getId()));

        assertEquals("Não é possível alterar venda cancelada", ex.getMessage());
    }

    @Test
    @DisplayName("Deve calcular valor total considerando itens cancelados")
    void deveCalcularValorTotalComItensCancelados() {
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);

        venda.cancelarItem(item1.getId());

        BigDecimal total = venda.calcularValorTotal();
        assertEquals(new BigDecimal("20.0"), total);
    }

    @Test
    @DisplayName("Deve retornar total de itens considerando itens cancelados")
    void deveRetornarTotalItensComItensCancelados() {
        venda.adicionarItem(item1);
        venda.adicionarItem(item2);

        venda.cancelarItem(item1.getId());

        int totalItens = venda.getTotalItens();
        assertEquals(1, totalItens);
    }

}
