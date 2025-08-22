package com.projeto.vendas.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

        org.assertj.core.api.Assertions.assertThat(health.getStatus().getCode())
                .isEqualTo("UP");

        org.assertj.core.api.Assertions.assertThat(health.getDetails())
                .containsKeys("service", "version", "timestamp");
    }
}
