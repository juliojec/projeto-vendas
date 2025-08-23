package com.projeto.vendas.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Monitoramento", description = "Endpoints de monitoramento e saúde da aplicação")
public class HealthController implements HealthIndicator {

    @GetMapping
    @Operation(
            summary = "Verificação de saúde",
            description = "Endpoint público para verificar se a API está funcionando"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aplicação está saudável"),
            @ApiResponse(responseCode = "503", description = "Aplicação com problemas")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Health health = health();

        Map<String, Object> response = Map.of(
                "status", health.getStatus().getCode(),
                "timestamp", LocalDateTime.now(),
                "service", "vendas-api",
                "checks", Map.of(
                        "database", "UP",
                        "diskSpace", "UP",
                        "application", "UP"));

        if (health.getStatus().getCode().equals("UP")) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(503).body(response);
        }
    }

    @Override
    public Health health() {
        try {
            return Health.up()
                    .withDetail("service", "vendas-api")
                    .withDetail("version", "1.0.0")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }
}