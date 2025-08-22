package com.projeto.vendas.domain.services;

import com.projeto.vendas.application.dtos.*;
import com.projeto.vendas.application.mappers.VendaMapper;
import com.projeto.vendas.domain.entities.ItemVenda;
import com.projeto.vendas.domain.entities.Venda;
import com.projeto.vendas.domain.repositories.VendaRepository;
import com.projeto.vendas.infrastructure.events.EventPublisher;
import com.projeto.vendas.infrastructure.events.VendaEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Teste da Service de Vendas")
class VendaServiceTest {

    private VendaRepository vendaRepository;
    private VendaMapper vendaMapper;
    private EventPublisher eventPublisher;
    private VendaService vendaService;

    @BeforeEach
    void setup() {
        vendaRepository = mock(VendaRepository.class);
        vendaMapper = mock(VendaMapper.class);
        eventPublisher = mock(EventPublisher.class);

        vendaService = new VendaService(vendaRepository, vendaMapper, eventPublisher);
    }

    private Venda vendaMock() {
        Venda venda = mock(Venda.class);
        when(venda.getItens()).thenReturn(List.of(mock(ItemVenda.class)));
        when(venda.calcularValorTotal()).thenReturn(BigDecimal.valueOf(100));
        when(venda.getTotalItens()).thenReturn(1);
        when(venda.getNumeroVenda()).thenReturn("VD-123");
        when(venda.getClienteId()).thenReturn("1");
        when(venda.getClienteNome()).thenReturn("Cliente Teste");
        return venda;
    }

    private VendaResponseDto vendaResponseMock() {
        return mock(VendaResponseDto.class);
    }

    @Test
    void criarVenda_deveSalvarEVincularEvento() {
        var cliente = new ClientExternalDto("1", "Cliente Teste");
        var filial = new FilialExternalDto("10", "Filial Teste");
        var item = new ItemVendaRequestDto(new ProdutoDto("156", "Teste"), 2, BigDecimal.valueOf(50));
        var request = new VendaRequestDto(cliente, filial, List.of(item));

        Venda venda = vendaMock();
        when(vendaMapper.toEntity(any(), anyString())).thenReturn(venda);
        when(vendaRepository.existsByNumeroVenda(anyString())).thenReturn(false);
        when(vendaRepository.save(venda)).thenReturn(venda);
        when(vendaMapper.toDto(venda)).thenReturn(vendaResponseMock());

        VendaResponseDto response = vendaService.criarVenda(request);

        assertNotNull(response);
        verify(vendaRepository).save(venda);
        verify(eventPublisher).publish(any(VendaEvents.CompraEfetuada.class));
    }

    @Test
    void buscarPorNumero_deveRetornarVenda() {
        Venda venda = vendaMock();
        when(vendaRepository.findByNumeroVenda("VD-123")).thenReturn(Optional.of(venda));
        when(vendaMapper.toDto(venda)).thenReturn(vendaResponseMock());

        VendaResponseDto dto = vendaService.buscarPorNumero("VD-123");
        assertNotNull(dto);
    }

    @Test
    void cancelarVenda_deveCancelarEVincularEvento() {
        Venda venda = vendaMock();
        when(vendaRepository.findByNumeroVenda("VD-123")).thenReturn(Optional.of(venda));
        when(vendaRepository.save(venda)).thenReturn(venda);
        when(vendaMapper.toDto(venda)).thenReturn(vendaResponseMock());

        VendaResponseDto response = vendaService.cancelarVenda("VD-123");

        assertNotNull(response);
        verify(vendaRepository).save(venda);
        verify(eventPublisher).publish(any(VendaEvents.CompraCancelada.class));
    }

    @Test
    void cancelarItem_deveCancelarItemEVincularEvento() {
        ItemVenda item = mock(ItemVenda.class);
        Venda venda = mock(Venda.class);

        when(venda.getItens()).thenReturn(List.of(item));
        when(item.getId()).thenReturn(1L);
        when(item.getProdutoId()).thenReturn("10");
        when(item.getProdutoDescricao()).thenReturn("Produto Teste");
        when(item.getQuantidade()).thenReturn(2);
        when(item.calcularValorTotal()).thenReturn(BigDecimal.valueOf(50));

        when(vendaRepository.findByNumeroVenda("VD-123")).thenReturn(Optional.of(venda));
        when(vendaRepository.save(venda)).thenReturn(venda);
        when(vendaMapper.toDto(venda)).thenReturn(vendaResponseMock());

        VendaResponseDto response = vendaService.cancelarItem("VD-123", 1L);

        assertNotNull(response);
        verify(vendaRepository).save(venda);
        verify(eventPublisher).publish(any(VendaEvents.ItemCancelado.class));
    }
}
