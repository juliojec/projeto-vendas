package com.projeto.vendas.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientExternalDto(
        @NotBlank(message = "ID é obrigatório")
        @Size(max = 50, message = "ID não pode ter mais de 50 caracteres")
        String id,

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome não pode ter mais de 255 caracteres")
        String nome
) {}