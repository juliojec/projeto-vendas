package com.projeto.vendas.application.mappers;

import com.projeto.vendas.application.dtos.*;
import com.projeto.vendas.domain.entities.Venda;
import com.projeto.vendas.domain.entities.ItemVenda;
import com.projeto.vendas.domain.enums.StatusVenda;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Deve Testar a Classe Venda Mapper")
class VendaMapperTest {

    private VendaMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new VendaMapper();
    }

    @Test
    void deveConverterVendaRequestDtoParaVenda() {
        var dto = new VendaRequestDto(
                new ClientExternalDto("C001", "Cliente Teste"),
                new FilialExternalDto("F001", "Filial Teste"),
                List.of(
                        new ItemVendaRequestDto(new ProdutoDto("P001", "Produto 1"), 2, new BigDecimal("50.00")),
                        new ItemVendaRequestDto(new ProdutoDto("P002", "Produto 2"), 1, new BigDecimal("100.00"))
                ));

        String numeroVenda = "VD-123";

        Venda venda = mapper.toEntity(dto, numeroVenda);

        assertThat(venda).isNotNull();
        assertThat(venda.getNumeroVenda()).isEqualTo(numeroVenda);
        assertThat(venda.getClienteId()).isEqualTo("C001");
        assertThat(venda.getClienteNome()).isEqualTo("Cliente Teste");
        assertThat(venda.getItens()).hasSize(2);

        // Verifica valor total
        BigDecimal totalEsperado = new BigDecimal("200.00"); // 2*50 + 1*100
        assertThat(venda.calcularValorTotal()).isEqualByComparingTo(totalEsperado);
    }

    @Test
    void deveConverterVendaParaVendaResponseDto() {
        Venda venda = new Venda("VD-123", "C001", "Cliente Teste", "F001", "Filial Teste");
        venda.adicionarItem(new ItemVenda("P001", "Produto 1", 2, new BigDecimal("50.00")));
        venda.adicionarItem(new ItemVenda("P002", "Produto 2", 1, new BigDecimal("100.00")));

        VendaResponseDto dto = mapper.toDto(venda);

        assertThat(dto).isNotNull();
        assertThat(dto.numeroVenda()).isEqualTo("VD-123");
        assertThat(dto.cliente().id()).isEqualTo("C001");
        assertThat(dto.cliente().nome()).isEqualTo("Cliente Teste");
        assertThat(dto.itens()).hasSize(2);

        BigDecimal totalEsperado = new BigDecimal("200.00");
        assertThat(dto.valorTotal()).isEqualByComparingTo(totalEsperado);

        assertThat(dto.status()).isEqualTo(StatusVenda.ATIVA);
        assertThat(dto.totalItens()).isEqualTo(3);
    }
}
