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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
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
    void criarVenda_semItens_deveLancarException() {
        var cliente = new ClientExternalDto("1", "Cliente Teste");
        var filial = new FilialExternalDto("10", "Filial Teste");

        var request = new VendaRequestDto(cliente, filial, Collections.emptyList());

        Venda venda = mock(Venda.class);
        when(venda.getItens()).thenReturn(Collections.emptyList());
        when(vendaMapper.toEntity(any(), anyString())).thenReturn(venda);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> vendaService.criarVenda(request)
        );

        assertEquals("Venda deve conter pelo menos um item", exception.getMessage());

        verify(vendaRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
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
    void listarVendas_deveRetornarPaginaDeVendas() {
        Pageable pageable = PageRequest.of(0, 10);

        Venda venda = vendaMock();
        VendaResponseDto dto = vendaResponseMock();

        Page<Venda> pageMock = new PageImpl<>(Collections.singletonList(venda));
        when(vendaRepository.findAll(pageable)).thenReturn(pageMock);

        when(vendaMapper.toDto(venda)).thenReturn(dto);

        Page<VendaResponseDto> response = vendaService.listarVendas(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertSame(dto, response.getContent().get(0));

        verify(vendaRepository, times(1)).findAll(pageable);
        verify(vendaMapper, times(1)).toDto(venda);
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

    @Test
    @DisplayName("buscarPorNumero deve lançar exceção quando venda não for encontrada")
    void buscarPorNumero_vendaNaoEncontrada_deveLancarExcecao() {
        String numeroVenda = "VD-999";

        when(vendaRepository.findByNumeroVenda(numeroVenda)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vendaService.buscarPorNumero(numeroVenda));

        assertEquals("Venda não encontrada: " + numeroVenda, exception.getMessage());

        verify(vendaMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("cancelarVenda deve lançar exceção quando venda não for encontrada")
    void cancelarVenda_vendaNaoEncontrada_deveLancarExcecao() {
        String numeroVenda = "VD-999";

        when(vendaRepository.findByNumeroVenda(numeroVenda)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vendaService.cancelarVenda(numeroVenda));

        assertEquals("Venda não encontrada: " + numeroVenda, exception.getMessage());

        verify(vendaRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("cancelarItem deve lançar exceção quando venda não for encontrada")
    void cancelarItem_vendaNaoEncontrada_deveLancarExcecao() {
        String numeroVenda = "VD-999";
        Long itemId = 1L;

        when(vendaRepository.findByNumeroVenda(numeroVenda)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vendaService.cancelarItem(numeroVenda, itemId));

        assertEquals("Venda não encontrada: " + numeroVenda, exception.getMessage());

        verify(vendaRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("cancelarItem deve lançar exceção quando item não for encontrado na venda")
    void cancelarItem_itemNaoEncontrado_deveLancarExcecao() {
        String numeroVenda = "VD-123";
        Long itemId = 999L;

        ItemVenda itemExistente = mock(ItemVenda.class);
        when(itemExistente.getId()).thenReturn(1L);

        Venda venda = mock(Venda.class);
        when(venda.getItens()).thenReturn(List.of(itemExistente));
        when(vendaRepository.findByNumeroVenda(numeroVenda)).thenReturn(Optional.of(venda));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> vendaService.cancelarItem(numeroVenda, itemId));

        assertEquals("Item não encontrado: " + itemId, exception.getMessage());

        verify(vendaRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("gerarNumeroVenda deve gerar novo número se já existir no repositório")
    void gerarNumeroVenda_deveGerarNovoNumeroSeJaExistir() {
        when(vendaRepository.existsByNumeroVenda(anyString()))
                .thenReturn(true)
                .thenReturn(false);

        var cliente = new ClientExternalDto("1", "Cliente Teste");
        var filial = new FilialExternalDto("10", "Filial Teste");
        var item = new ItemVendaRequestDto(new ProdutoDto("156", "Teste"), 2, BigDecimal.valueOf(50));
        var request = new VendaRequestDto(cliente, filial, List.of(item));

        Venda venda = vendaMock();
        when(vendaMapper.toEntity(any(), anyString())).thenReturn(venda);
        when(vendaRepository.save(venda)).thenReturn(venda);
        when(vendaMapper.toDto(venda)).thenReturn(vendaResponseMock());

        VendaResponseDto response = vendaService.criarVenda(request);

        assertNotNull(response);

        verify(vendaRepository, atLeast(2)).existsByNumeroVenda(anyString());
    }
}
