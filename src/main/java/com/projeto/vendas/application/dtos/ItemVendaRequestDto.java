package com.projeto.vendas.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ItemVendaRequestDto(
        @NotNull(message = "Produto é obrigatório")
        @Valid
        ProdutoDto produto,

        @NotNull(message = "Quantidade é obrigatória")
        @Min(value = 1, message = "Quantidade deve ser maior que zero")
        @Max(value = 20, message = "Quantidade não pode ser maior que 20")
        Integer quantidade,

        @NotNull(message = "Valor unitário é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor unitário deve ser maior que zero")
        BigDecimal valorUnitario
) {}