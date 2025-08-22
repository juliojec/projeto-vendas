package com.projeto.vendas.infrastructure.events;

import com.projeto.vendas.infrastructure.observability.PerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisher {

    private final PerformanceService metricsService;

    public void publish(Object event) {
        try {
            switch (event) {
                case VendaEvents.CompraEfetuada compra -> logCompraEfetuada(compra);
                case VendaEvents.CompraAlterada alteracao -> logCompraAlterada(alteracao);
                case VendaEvents.CompraCancelada cancelamento -> logCompraCancelada(cancelamento);
                case VendaEvents.ItemCancelado itemCancelado -> logItemCancelado(itemCancelado);
                default -> logEventoGenerico(event);
            }
        } catch (Exception e) {
            log.error("Erro ao publicar evento: {}", event.getClass().getSimpleName(), e);
        }
    }

    private void logCompraEfetuada(VendaEvents.CompraEfetuada evento) {
        metricsService.incrementCounter("vendas_eventos_total", "evento", "criada");
        log.info("COMPRA EFETUADA | Venda: {} | Cliente: {} ({}) | Valor: R$ {} | Itens: {} | Data: {}",
                evento.getNumeroVenda(),
                evento.getClienteNome(),
                evento.getClienteId(),
                evento.getValorTotal(),
                evento.getTotalItens(),
                evento.getTimestamp()
        );
    }

    private void logCompraCancelada(VendaEvents.CompraCancelada evento) {
        metricsService.incrementCounter("vendas_eventos_total", "evento", "cancelada");
        log.warn("COMPRA CANCELADA | Venda: {} | Cliente: {} ({}) | Valor Cancelado: R$ {} | Motivo: {} | Data: {}",
                evento.getNumeroVenda(),
                evento.getClienteNome(),
                evento.getClienteId(),
                evento.getValorCancelado(),
                evento.getMotivo(),
                evento.getTimestamp());
    }

    private void logItemCancelado(VendaEvents.ItemCancelado evento) {
        log.info("ITEM CANCELADO | Venda: {} | Item ID: {} | Produto: {} ({}) | Qtd: {} | Valor: R$ {} | Data: {}",
                evento.getNumeroVenda(),
                evento.getItemId(),
                evento.getProdutoDescricao(),
                evento.getProdutoId(),
                evento.getQuantidade(),
                evento.getValorCancelado(),
                evento.getTimestamp());
    }

    private void logCompraAlterada(VendaEvents.CompraAlterada evento) {
        log.info("COMPRA ALTERADA | Venda: {} | Tipo: {} | Detalhes: {} | Data: {}",
                evento.getNumeroVenda(),
                evento.getTipoAlteracao(),
                evento.getDetalhes(),
                evento.getTimestamp());
    }

    private void logEventoGenerico(Object evento) {
        log.info("EVENTO | Tipo: {} | Dados: {}",
                evento.getClass().getSimpleName(),
                evento);
    }
}


