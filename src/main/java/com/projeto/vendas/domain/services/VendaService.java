package com.projeto.vendas.domain.services;

import com.projeto.vendas.application.dtos.VendaRequestDto;
import com.projeto.vendas.application.dtos.VendaResponseDto;
import com.projeto.vendas.application.mappers.VendaMapper;
import com.projeto.vendas.domain.entities.ItemVenda;
import com.projeto.vendas.domain.entities.Venda;
import com.projeto.vendas.domain.repositories.VendaRepository;
import com.projeto.vendas.infrastructure.events.EventPublisher;
import com.projeto.vendas.infrastructure.events.VendaEvents;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VendaService {

    private final VendaRepository vendaRepository;
    private final VendaMapper vendaMapper;
    private final EventPublisher eventPublisher;

    public VendaResponseDto criarVenda(VendaRequestDto request) {
        log.info("Criando venda para cliente: {}", request.cliente().id());

        String numeroVenda = gerarNumeroVenda();
        Venda venda = vendaMapper.toEntity(request, numeroVenda);

        validarVenda(venda);

        Venda vendaSalva = vendaRepository.save(venda);

        eventPublisher.publish(new VendaEvents.CompraEfetuada(
            vendaSalva.getNumeroVenda(),
            vendaSalva.getClienteId(),
            vendaSalva.getClienteNome(),
            vendaSalva.calcularValorTotal(),
            vendaSalva.getTotalItens()));

        log.info("Venda criada: {}", vendaSalva.getNumeroVenda());
        return vendaMapper.toDto(vendaSalva);
    }

    @Transactional(readOnly = true)
    public VendaResponseDto buscarPorNumero(String numeroVenda) {
        log.info("Buscando venda: {}", numeroVenda);

        Venda venda = vendaRepository.findByNumeroVenda(numeroVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + numeroVenda));

        return vendaMapper.toDto(venda);
    }

    @Transactional(readOnly = true)
    public Page<VendaResponseDto> listarVendas(Pageable pageable) {
        return vendaRepository.findAll(pageable)
                .map(vendaMapper::toDto);
    }

    public VendaResponseDto cancelarVenda(String numeroVenda) {
        log.info("Cancelando venda: {}", numeroVenda);

        Venda venda = vendaRepository.findByNumeroVenda(numeroVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + numeroVenda));

        var valorOriginal = venda.calcularValorTotal();

        venda.cancelar();
        Venda vendaSalva = vendaRepository.save(venda);

        eventPublisher.publish(new VendaEvents.CompraCancelada(
            vendaSalva.getNumeroVenda(),
            vendaSalva.getClienteId(),
            vendaSalva.getClienteNome(),
            valorOriginal,
            "Cancelamento via API"));

        log.info("Venda cancelada: {}", numeroVenda);
        return vendaMapper.toDto(vendaSalva);
    }

    public VendaResponseDto cancelarItem(String numeroVenda, Long itemId) {
        log.info("Cancelando item {} da venda: {}", itemId, numeroVenda);

        Venda venda = vendaRepository.findByNumeroVenda(numeroVenda)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada: " + numeroVenda));

        ItemVenda item = venda.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));

        var valorItemOriginal = item.calcularValorTotal();

        venda.cancelarItem(itemId);
        Venda vendaSalva = vendaRepository.save(venda);

        eventPublisher.publish(new VendaEvents.ItemCancelado(
                vendaSalva.getNumeroVenda(),
                itemId,
                item.getProdutoId(),
                item.getProdutoDescricao(),
                item.getQuantidade(),
                valorItemOriginal));

        log.info("Item {} cancelado da venda: {}", itemId, numeroVenda);
        return vendaMapper.toDto(vendaSalva);
    }

    private String gerarNumeroVenda() {
        String numeroVenda;
        do {
            numeroVenda = "VD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (vendaRepository.existsByNumeroVenda(numeroVenda));

        return numeroVenda;
    }

    private void validarVenda(Venda venda) {
        if (venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Venda deve conter pelo menos um item");
        }
    }
}