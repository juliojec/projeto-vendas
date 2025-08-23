package com.projeto.vendas.web.controllers;

import com.projeto.vendas.application.dtos.*;
import com.projeto.vendas.domain.enums.StatusVenda;
import com.projeto.vendas.domain.services.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Vendas Controller")
class VendasControllerTest {

    private VendaService vendaService;
    private VendasController controller;

    @BeforeEach
    void setup() {
        vendaService = mock(VendaService.class);
        controller = new VendasController(vendaService);
    }

    private VendaResponseDto vendaMock() {
        ClientExternalDto cliente = new ClientExternalDto("1", "Cliente");
        FilialExternalDto filial = new FilialExternalDto("10", "Filial");
        ItemVendaResponseDto item = new ItemVendaResponseDto(1L,
                new ProdutoDto("1", "Produto"), 2,
                BigDecimal.valueOf(50), BigDecimal.ZERO,
                BigDecimal.valueOf(100), false);

        return new VendaResponseDto(1L, "VD-123", LocalDateTime.now(),
                cliente, filial, List.of(item), BigDecimal.valueOf(100),
                StatusVenda.ATIVA, 1);
    }

    @Test
    void criarVenda_deveRetornarCreated() {
        VendaRequestDto request = new VendaRequestDto(
                new ClientExternalDto("1", "Cliente"),
                new FilialExternalDto("10", "Filial"),
                List.of(new ItemVendaRequestDto( new ProdutoDto("1", "Produto"), 2, BigDecimal.valueOf(50))));

        when(vendaService.criarVenda(request)).thenReturn(vendaMock());

        ResponseEntity<VendaResponseDto> response = controller.criarVenda(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("VD-123", response.getBody().numeroVenda());
    }

    @Test
    void listarVendas_deveRetornarOk() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<VendaResponseDto> pageMock = new PageImpl<>(Collections.singletonList(vendaMock()));

        when(vendaService.listarVendas(pageable)).thenReturn(pageMock);

        ResponseEntity<Page<VendaResponseDto>> response = controller.listarVendas(pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("VD-123", response.getBody().getContent().get(0).numeroVenda());

        verify(vendaService, times(1)).listarVendas(pageable);
    }

    @Test
    void buscarVenda_deveRetornarOk() {
        when(vendaService.buscarPorNumero("VD-123")).thenReturn(vendaMock());

        ResponseEntity<VendaResponseDto> response = controller.buscarVenda("VD-123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("VD-123", response.getBody().numeroVenda());
    }

    @Test
    void cancelarVenda_deveRetornarOk() {
        when(vendaService.cancelarVenda("VD-123")).thenReturn(vendaMock());

        ResponseEntity<VendaResponseDto> response = controller.cancelarVenda("VD-123");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("VD-123", response.getBody().numeroVenda());
    }

    @Test
    void cancelarItem_deveRetornarOk() {
        Long itemId = 1L;
        String numeroVenda = "VD-123";

        when(vendaService.cancelarItem(numeroVenda, itemId)).thenReturn(vendaMock());

        ResponseEntity<VendaResponseDto> response = controller.cancelarItem(numeroVenda, itemId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("VD-123", response.getBody().numeroVenda());

        verify(vendaService, times(1)).cancelarItem(numeroVenda, itemId);
    }

    @Test
    @DisplayName("criarVenda deve retornar BadRequest quando ocorrer exceção")
    void criarVenda_deveRetornarBadRequest_quandoException() {
        VendaRequestDto request = new VendaRequestDto(
                new ClientExternalDto("1", "Cliente"),
                new FilialExternalDto("10", "Filial"),
                List.of(new ItemVendaRequestDto(new ProdutoDto("1", "Produto"), 2, BigDecimal.valueOf(50))));

        when(vendaService.criarVenda(request)).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<VendaResponseDto> response = controller.criarVenda(request);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }

    @Test
    @DisplayName("buscarVenda deve retornar NotFound quando ocorrer RuntimeException")
    void buscarVenda_deveRetornarNotFound_quandoRuntimeException() {
        String numeroVenda = "VD-123";
        when(vendaService.buscarPorNumero(numeroVenda)).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<VendaResponseDto> response = controller.buscarVenda(numeroVenda);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }

    @Test
    @DisplayName("cancelarVenda deve retornar BadRequest quando ocorrer Exception")
    void cancelarVenda_deveRetornarBadRequest_quandoException() {
        String numeroVenda = "VD-123";
        when(vendaService.cancelarVenda(numeroVenda)).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<VendaResponseDto> response = controller.cancelarVenda(numeroVenda);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }

    @Test
    @DisplayName("cancelarItem deve retornar BadRequest quando ocorrer Exception")
    void cancelarItem_deveRetornarBadRequest_quandoException() {
        String numeroVenda = "VD-123";
        Long itemId = 1L;
        when(vendaService.cancelarItem(numeroVenda, itemId)).thenThrow(new RuntimeException("Erro simulado"));

        ResponseEntity<VendaResponseDto> response = controller.cancelarItem(numeroVenda, itemId);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }

}
