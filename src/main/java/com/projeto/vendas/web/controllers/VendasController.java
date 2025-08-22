package com.projeto.vendas.web.controllers;

import com.projeto.vendas.application.dtos.VendaRequestDto;
import com.projeto.vendas.application.dtos.VendaResponseDto;
import com.projeto.vendas.domain.services.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VendasController {

    private final VendaService vendaService;

    @PostMapping
    public ResponseEntity<VendaResponseDto> criarVenda(@Valid @RequestBody VendaRequestDto request) {
        try {
            VendaResponseDto venda = vendaService.criarVenda(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(venda);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{numeroVenda}")
    public ResponseEntity<VendaResponseDto> buscarVenda(@PathVariable String numeroVenda) {
        try {
            VendaResponseDto venda = vendaService.buscarPorNumero(numeroVenda);
            return ResponseEntity.ok(venda);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<VendaResponseDto>> listarVendas(@PageableDefault(page = 0, size = 10, sort = {"dataVenda", "id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<VendaResponseDto> vendas = vendaService.listarVendas(pageable);
        return ResponseEntity.ok(vendas);
    }

    @PutMapping("/{numeroVenda}/cancelar")
    public ResponseEntity<VendaResponseDto> cancelarVenda(@PathVariable String numeroVenda) {
        try {
            VendaResponseDto venda = vendaService.cancelarVenda(numeroVenda);
            return ResponseEntity.ok(venda);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{numeroVenda}/itens/{itemId}/cancelar")
    public ResponseEntity<VendaResponseDto> cancelarItem(@PathVariable String numeroVenda, @PathVariable Long itemId) {
        try {
            VendaResponseDto venda = vendaService.cancelarItem(numeroVenda, itemId);
            return ResponseEntity.ok(venda);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}