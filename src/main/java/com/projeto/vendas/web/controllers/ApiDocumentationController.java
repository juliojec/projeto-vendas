package com.projeto.vendas.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Documentação", description = "Endpoints de informações e documentação da API")
public class ApiDocumentationController {

    @GetMapping("/info")
    @Operation(
        summary = "Informações da API",
        description = "Retorna informações básicas sobre a API de vendas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações retornadas com sucesso")
    })
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = Map.of(
            "name", "Vendas API",
            "version", "1.0.0",
            "description", "API REST para gerenciamento de vendas - Sistema 123Vendas",
            "timestamp", LocalDateTime.now(),
            "status", "active",
            "documentation", "/swagger-ui.html",
            "health", "/actuator/health",
            "metrics", "/actuator/prometheus");

        return ResponseEntity.ok(info);
    }

    @GetMapping("/status")
    @Operation(
        summary = "Status da aplicação",
        description = "Retorna o status básico da aplicação (público)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retornado com sucesso")
    })
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "service", "vendas-api",
            "version", "1.0.0");

        return ResponseEntity.ok(status);
    }

}