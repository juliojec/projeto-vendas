package com.projeto.vendas.web.controllers;

import com.projeto.vendas.application.dtos.*;
import com.projeto.vendas.domain.enums.StatusVenda;
import com.projeto.vendas.domain.services.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
}
