package com.projeto.vendas.application.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record VendaRequestDto(
        @NotNull @Valid ClientExternalDto cliente,
        @NotNull @Valid FilialExternalDto filial,
        @NotEmpty @Valid List<ItemVendaRequestDto> itens
) {}
