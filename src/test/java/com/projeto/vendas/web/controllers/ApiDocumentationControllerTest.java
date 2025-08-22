package com.projeto.vendas.web.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Deve Testar a Classe de Controller Api Documentation")
@WebMvcTest(ApiDocumentationController.class)
class ApiDocumentationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornarInformacoesDaApi() throws Exception {
        mockMvc.perform(get("/api/v1/info")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vendas API"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.description").value("API REST para gerenciamento de vendas - Sistema 123Vendas"))
                .andExpect(jsonPath("$.status").value("active"))
                .andExpect(jsonPath("$.documentation").value("/swagger-ui.html"))
                .andExpect(jsonPath("$.health").value("/actuator/health"))
                .andExpect(jsonPath("$.metrics").value("/actuator/prometheus"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void deveRetornarStatusDaAplicacao() throws Exception {
        mockMvc.perform(get("/api/v1/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("vendas-api"))
                .andExpect(jsonPath("$.version").value("1.0.0"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
