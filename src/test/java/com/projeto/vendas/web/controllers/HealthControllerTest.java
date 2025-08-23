package com.projeto.vendas.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Deve Testar a Classe de Controller Health")
@WebMvcTest(controllers = HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarStatusUp() throws Exception {
        mockMvc.perform(get("/api/v1/health")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("vendas-api"))
                .andExpect(jsonPath("$.checks.database").value("UP"))
                .andExpect(jsonPath("$.checks.diskSpace").value("UP"))
                .andExpect(jsonPath("$.checks.application").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void metodoHealthDeveRetornarUp() {
        HealthController controller = new HealthController();
        var health = controller.health();

        assertThat(health.getStatus().getCode())
                .isEqualTo("UP");

        assertThat(health.getDetails())
                .containsKeys("service", "version", "timestamp");
    }

    @Test
    void deveRetornarStatusDown() {
        HealthController controller = new HealthController() {
            @Override
            public org.springframework.boot.actuate.health.Health health() {
                return org.springframework.boot.actuate.health.Health.down()
                        .withDetail("error", "Banco de dados indisponível")
                        .withDetail("timestamp", LocalDateTime.now())
                        .build();
            }
        };

        ResponseEntity<Map<String, Object>> response = controller.healthCheck();

        assertThat(response.getStatusCodeValue()).isEqualTo(503);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("DOWN");
        assertThat(((Map<?, ?>) response.getBody().get("checks")).get("database")).isEqualTo("UP");
    }

    @Test
    void metodoHealthDeveRetornarDownQuandoExcecao() {
        HealthController controller = new HealthController() {
            @Override
            public Health health() {
                try {
                    throw new RuntimeException("Erro simulado");
                } catch (Exception e) {
                    return Health.down()
                            .withDetail("error", e.getMessage())
                            .build();
                }
            }
        };

        Health health = controller.health();

        assertThat(health.getStatus().getCode()).isEqualTo("DOWN");

        assertThat(health.getDetails())
                .containsEntry("error", "Erro simulado");
    }

}
