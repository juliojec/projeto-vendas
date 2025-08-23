package com.projeto.vendas.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Deve Testar a Entidade Item Venda")
class ItemVendaTest {

    @Test
    void deveCriarItemComDescontoCorreto() {
        // quantidade < 4 => sem desconto
        ItemVenda item1 = new ItemVenda("P001", "Produto 1", 3, new BigDecimal("10.0"));
        assertEquals(0, item1.getDescontoPercentual().compareTo(BigDecimal.ZERO));
        assertEquals(0, item1.calcularValorTotal().compareTo(new BigDecimal("30.0")));

        // quantidade >= 4 e < 10 => 10% de desconto
        ItemVenda item2 = new ItemVenda("P002", "Produto 2", 5, new BigDecimal("20.0"));
        assertEquals(0, item2.getDescontoPercentual().compareTo(new BigDecimal("10.00")));
        BigDecimal valorEsperado2 = new BigDecimal("100.0").subtract(new BigDecimal("10.0")); // 10% de 100
        assertEquals(0, item2.calcularValorTotal().compareTo(valorEsperado2));

        // quantidade >= 10 => 20% de desconto
        ItemVenda item3 = new ItemVenda("P003", "Produto 3", 12, new BigDecimal("5.0"));
        assertEquals(0, item3.getDescontoPercentual().compareTo(new BigDecimal("20.00")));
        BigDecimal valorEsperado3 = new BigDecimal("60.0").subtract(new BigDecimal("12.0")); // 20% de 60
        assertEquals(0, item3.calcularValorTotal().compareTo(valorEsperado3));
    }


    @Test
    void deveValidarQuantidade() {
        // quantidade <= 0
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> new ItemVenda("P004", "Produto 4", 0, BigDecimal.TEN));
        assertEquals("Quantidade deve ser maior que zero", ex1.getMessage());

        // quantidade > 20
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> new ItemVenda("P005", "Produto 5", 25, BigDecimal.TEN));
        assertEquals("Não é possível vender acima de 20 itens iguais", ex2.getMessage());
    }

    @Test
    void deveCalcularValorTotalCorretamente() {
        ItemVenda item = new ItemVenda("P006", "Produto 6", 4, new BigDecimal("50.0"));
        // 4 itens, 10% de desconto
        BigDecimal valorBase = new BigDecimal("200.0");
        BigDecimal desconto = valorBase.multiply(new BigDecimal("0.10"));
        BigDecimal valorTotal = valorBase.subtract(desconto);

        assertEquals(valorTotal, item.calcularValorTotal());
    }

    @Test
    void deveCancelarItem() {
        ItemVenda item = new ItemVenda("P007", "Produto 7", 2, BigDecimal.TEN);
        assertFalse(item.getCancelado());
        item.cancelar();
        assertTrue(item.getCancelado());
    }

    @Test
    void deveLancarExcecaoParaQuantidadeNula() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new ItemVenda("P008", "Produto 8", null, BigDecimal.TEN));
        assertEquals("Quantidade deve ser maior que zero", ex.getMessage());
    }
}
