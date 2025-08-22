package com.projeto.vendas.domain.entities;

import com.projeto.vendas.domain.enums.StatusVenda;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vendas")
@Getter
@Setter
@NoArgsConstructor
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_venda", unique = true, nullable = false)
    private String numeroVenda;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @Column(name = "cliente_id", nullable = false)
    private String clienteId;

    @Column(name = "cliente_nome", nullable = false)
    private String clienteNome;

    @Column(name = "filial_id", nullable = false)
    private String filialId;

    @Column(name = "filial_nome", nullable = false)
    private String filialNome;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false)
    private Boolean cancelada = false;

    public Venda(String numeroVenda, String clienteId, String clienteNome, String filialId, String filialNome) {
        this.numeroVenda = numeroVenda;
        this.clienteId = clienteId;
        this.clienteNome = clienteNome;
        this.filialId = filialId;
        this.filialNome = filialNome;
        this.dataVenda = LocalDateTime.now();
    }

    public void adicionarItem(ItemVenda item) {
        if (cancelada) {
            throw new IllegalStateException("Não é possível alterar venda cancelada");
        }
        item.setVenda(this);
        this.itens.add(item);
    }

    public void cancelar() {
        if (cancelada) {
            throw new IllegalStateException("Venda já está cancelada");
        }
        this.cancelada = true;
        this.itens.forEach(ItemVenda::cancelar);
    }

    public void cancelarItem(Long itemId) {
        if (cancelada) {
            throw new IllegalStateException("Não é possível alterar venda cancelada");
        }

        ItemVenda item = itens.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        item.cancelar();
    }

    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .filter(item -> !item.getCancelado())
                .map(ItemVenda::calcularValorTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public StatusVenda getStatus() {
        return cancelada ? StatusVenda.CANCELADA : StatusVenda.ATIVA;
    }

    public Integer getTotalItens() {
        return itens.stream()
                .filter(item -> !item.getCancelado())
                .mapToInt(ItemVenda::getQuantidade)
                .sum();
    }
}