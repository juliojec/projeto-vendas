package com.projeto.vendas.application.dtos;

import jakarta.validation.constraints.NotBlank;

public record ProdutoDto(
        @NotBlank String id,
        @NotBlank String nome
) {}