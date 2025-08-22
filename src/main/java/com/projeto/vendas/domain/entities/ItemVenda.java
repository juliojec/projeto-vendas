package com.projeto.vendas.domain.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Entity
@Table(name = "itens_venda")
@Getter
@Setter
@NoArgsConstructor
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_id", nullable = false)
    private String produtoId;

    @Column(name = "produto_descricao", nullable = false)
    private String produtoDescricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "desconto_percentual", precision = 5, scale = 2)
    private BigDecimal descontoPercentual = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean cancelado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    public ItemVenda(String produtoId, String produtoDescricao, Integer quantidade, BigDecimal valorUnitario) {
        this.produtoId = produtoId;
        this.produtoDescricao = produtoDescricao;
        this.quantidade = validarQuantidade(quantidade);
        this.valorUnitario = valorUnitario;
        this.descontoPercentual = calcularDesconto(quantidade);
    }

    private Integer validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (quantidade > 20) {
            throw new IllegalArgumentException("Não é possível vender acima de 20 itens iguais");
        }
        return quantidade;
    }

    private BigDecimal calcularDesconto(Integer quantidade) {
        if (quantidade >= 10) return new BigDecimal("20.00");
        if (quantidade >= 4) return new BigDecimal("10.00");
        return BigDecimal.ZERO;
    }

    public BigDecimal calcularValorTotal() {
        BigDecimal valorBase = valorUnitario.multiply(new BigDecimal(quantidade));
        BigDecimal desconto = valorBase.multiply(descontoPercentual.divide(new BigDecimal("100")));
        return valorBase.subtract(desconto);
    }

    public void cancelar() {
        this.cancelado = true;
    }
}