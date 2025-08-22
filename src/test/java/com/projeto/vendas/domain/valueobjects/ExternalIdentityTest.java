package com.projeto.vendas.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do Value Object ExternalIdentity")
class ExternalIdentityTest {

    @Nested
    @DisplayName("Criação")
    class Criacao {

        @Test
        @DisplayName("Deve criar ExternalIdentity com dados válidos")
        void deveCriarExternalIdentityComDadosValidos() {
            ClientExternal identity = new ClientExternal("CLI001", "João Silva");

            assertEquals("CLI001", identity.getId());
            assertEquals("João Silva", identity.getDescription());
        }

        @Test
        @DisplayName("Deve falhar com ID nulo")
        void deveFalharComIdNulo() {
            assertThrows(NullPointerException.class, () -> {
                new ClientExternal(null, "Descrição");
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição nula")
        void deveFalharComDescricaoNula() {
            assertThrows(NullPointerException.class, () -> {
                new ClientExternal("ID001", null);
            });
        }

        @Test
        @DisplayName("Deve falhar com ID vazio")
        void deveFalharComIdVazio() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ClientExternal("", "Descrição");
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição vazia")
        void deveFalharComDescricaoVazia() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ClientExternal("ID001", "");
            });
        }

        @Test
        @DisplayName("Deve falhar com ID apenas espaços")
        void deveFalharComIdApenasEspacos() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ClientExternal("   ", "Descrição");
            });
        }

        @Test
        @DisplayName("Deve falhar com descrição apenas espaços")
        void deveFalharComDescricaoApenasEspacos() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ClientExternal("ID001", "   ");
            });
        }
    }

    @Nested
    @DisplayName("Igualdade")
    class Igualdade {

        @Test
        @DisplayName("Deve ser igual quando IDs são iguais")
        void deveSerIgualQuandoIdsIguais() {
            ClientExternal identity1 = new ClientExternal("CLI001", "João Silva");
            ClientExternal identity2 = new ClientExternal("CLI001", "João Santos"); // Descrição diferente

            assertEquals(identity1, identity2);
            assertEquals(identity1.hashCode(), identity2.hashCode());
        }

        @Test
        @DisplayName("Deve ser diferente quando IDs são diferentes")
        void deveSerDiferenteQuandoIdsDiferentes() {
            ClientExternal identity1 = new ClientExternal("CLI001", "João Silva");
            ClientExternal identity2 = new ClientExternal("CLI002", "João Silva"); // Mesmo nome, ID diferente

            assertNotEquals(identity1, identity2);
        }

        @Test
        @DisplayName("Deve ser igual a si mesmo")
        void deveSerIgualASiMesmo() {
            ClientExternal identity = new ClientExternal("CLI001", "João Silva");

            assertEquals(identity, identity);
        }

        @Test
        @DisplayName("Não deve ser igual a null")
        void naoDeveSerIgualANull() {
            ClientExternal identity = new ClientExternal("CLI001", "João Silva");

            assertNotEquals(identity, null);
        }

        @Test
        @DisplayName("Não deve ser igual a objeto de outra classe")
        void naoDeveSerIgualAObjetoDeOutraClasse() {
            ClientExternal identity = new ClientExternal("CLI001", "João Silva");
            String string = "CLI001";

            assertNotEquals(identity, string);
        }
    }
}