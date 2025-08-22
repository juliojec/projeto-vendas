package com.projeto.vendas.application.mappers;

import com.projeto.vendas.application.dtos.*;
import com.projeto.vendas.domain.entities.ItemVenda;
import com.projeto.vendas.domain.entities.Venda;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class VendaMapper {

    public Venda toEntity(VendaRequestDto dto, String numeroVenda) {
        if (dto == null) {
            throw new IllegalArgumentException("VendaRequestDto não pode ser nulo");
        }

        log.debug("Convertendo VendaRequestDto para entidade: numeroVenda={}", numeroVenda);

        Venda venda = new Venda(
            numeroVenda,
            dto.cliente().id(),
            dto.cliente().nome(),
            dto.filial().id(),
            dto.filial().nome());

        dto.itens().forEach(itemDto -> {
            ItemVenda item = toEntity(itemDto);
            venda.adicionarItem(item);
        });

        log.debug("Venda convertida com {} itens", venda.getItens().size());
        return venda;
    }

    public VendaResponseDto toDto(Venda venda) {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }

        log.debug("Convertendo Venda para DTO: numeroVenda={}", venda.getNumeroVenda());

        List<ItemVendaResponseDto> itensDto = venda.getItens().stream()
                .map(this::toDto)
                .toList();

        return new VendaResponseDto(
                venda.getId(),
                venda.getNumeroVenda(),
                venda.getDataVenda(),
                toClienteDto(venda),
                toFilialDto(venda),
                itensDto,
                venda.calcularValorTotal(),
                venda.getStatus(),
                venda.getTotalItens());
    }

    private ItemVenda toEntity(ItemVendaRequestDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ItemVendaRequestDto não pode ser nulo");
        }

        return new ItemVenda(
            dto.produto().id(),
            dto.produto().nome(),
            dto.quantidade(),
            dto.valorUnitario());
    }

    private ItemVendaResponseDto toDto(ItemVenda item) {
        if (item == null) {
            throw new IllegalArgumentException("ItemVenda não pode ser nulo");
        }

        return new ItemVendaResponseDto(
                item.getId(),
                new ProdutoDto(item.getProdutoId(), item.getProdutoDescricao()),
                item.getQuantidade(),
                item.getValorUnitario(),
                item.getDescontoPercentual(),
                item.calcularValorTotal(),
                item.getCancelado());
    }

    private ClientExternalDto toClienteDto(Venda venda) {
        return new ClientExternalDto(venda.getClienteId(), venda.getClienteNome());
    }

    private FilialExternalDto toFilialDto(Venda venda) {
        return new FilialExternalDto(venda.getFilialId(), venda.getFilialNome());
    }

}