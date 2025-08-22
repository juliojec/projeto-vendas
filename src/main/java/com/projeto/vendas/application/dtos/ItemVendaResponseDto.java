package com.projeto.vendas.application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public record ItemVendaResponseDto(
        Long id,
        ProdutoDto produto,
        Integer quantidade,

        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
        BigDecimal valorUnitario,

        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
        BigDecimal descontoPercentual,

        @JsonFormat(shape = JsonFormat.Shape.NUMBER, pattern = "0.00")
        BigDecimal valorTotal,

        Boolean cancelado
) {}