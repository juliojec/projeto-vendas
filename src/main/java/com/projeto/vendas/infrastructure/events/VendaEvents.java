package com.projeto.vendas.infrastructure.events;

import java.math.BigDecimal;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class VendaEvents {

    @Data
    public static class CompraEfetuada {
        private final String numeroVenda;
        private final String clienteId;
        private final String clienteNome;
        private final BigDecimal valorTotal;
        private final Integer totalItens;
        private final LocalDateTime timestamp;

        public CompraEfetuada(String numeroVenda, String clienteId, String clienteNome,
                              BigDecimal valorTotal, Integer totalItens) {
            this.numeroVenda = numeroVenda;
            this.clienteId = clienteId;
            this.clienteNome = clienteNome;
            this.valorTotal = valorTotal;
            this.totalItens = totalItens;
            this.timestamp = LocalDateTime.now();
        }
    }

    @Data
    public static class CompraAlterada {
        private final String numeroVenda;
        private final String tipoAlteracao;
        private final String detalhes;
        private final LocalDateTime timestamp;

        public CompraAlterada(String numeroVenda, String tipoAlteracao, String detalhes) {
            this.numeroVenda = numeroVenda;
            this.tipoAlteracao = tipoAlteracao;
            this.detalhes = detalhes;
            this.timestamp = LocalDateTime.now();
        }
    }

    @Data
    public static class CompraCancelada {
        private final String numeroVenda;
        private final String clienteId;
        private final String clienteNome;
        private final BigDecimal valorCancelado;
        private final String motivo;
        private final LocalDateTime timestamp;

        public CompraCancelada(String numeroVenda, String clienteId, String clienteNome,
                               BigDecimal valorCancelado, String motivo) {
            this.numeroVenda = numeroVenda;
            this.clienteId = clienteId;
            this.clienteNome = clienteNome;
            this.valorCancelado = valorCancelado;
            this.motivo = motivo;
            this.timestamp = LocalDateTime.now();
        }
    }

    @Data
    public static class ItemCancelado {
        private final String numeroVenda;
        private final Long itemId;
        private final String produtoId;
        private final String produtoDescricao;
        private final Integer quantidade;
        private final BigDecimal valorCancelado;
        private final LocalDateTime timestamp;

        public ItemCancelado(String numeroVenda, Long itemId, String produtoId,
                             String produtoDescricao, Integer quantidade, BigDecimal valorCancelado) {
            this.numeroVenda = numeroVenda;
            this.itemId = itemId;
            this.produtoId = produtoId;
            this.produtoDescricao = produtoDescricao;
            this.quantidade = quantidade;
            this.valorCancelado = valorCancelado;
            this.timestamp = LocalDateTime.now();
        }
    }
}