package com.projeto.vendas.application.dtos;

import com.projeto.vendas.domain.enums.StatusVenda;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDto(
        Long id,
        String numeroVenda,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataVenda,

        ClientExternalDto cliente,
        FilialExternalDto filial,
        List<ItemVendaResponseDto> itens,

        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
        BigDecimal valorTotal,

        StatusVenda status,

        Integer totalItens
) {}