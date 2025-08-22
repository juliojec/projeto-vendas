package com.projeto.vendas.infrastructure.observability;

import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Deve Testar a Classe de Performance Aspect")
class PerformanceAspectTest {

    private PerformanceService metricsService;
    private PerformanceAspect aspect;
    private ProceedingJoinPoint joinPoint;
    private Signature signature;
    private Timer.Sample sample;

    @BeforeEach
    void setUp() {
        metricsService = Mockito.mock(PerformanceService.class);
        aspect = new PerformanceAspect(metricsService);

        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        signature = Mockito.mock(Signature.class);
        sample = Mockito.mock(Timer.Sample.class);

        when(metricsService.startTimer()).thenReturn(sample);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findAll");
        when(joinPoint.getTarget()).thenReturn(new Object() {
            @Override
            public String toString() {
                return "FakeRepository";
            }
        });
    }

    @Test
    void deveMonitorarRepository_comSucesso() throws Throwable {
        when(joinPoint.proceed()).thenReturn("resultado");

        Object result = aspect.monitorRepository(joinPoint);

        verify(metricsService).recordDatabaseQuery(anyString(), anyString(), anyLong());
        verify(metricsService).stopTimer(eq(sample), eq("repository_method_time"),
                any(), any(), any(), any());
        verify(metricsService, never()).incrementCounter(eq("repository_errors"), any(), any(), any(), any(), any());

        assert result.equals("resultado");
    }

    @Test
    void monitorRepository_deveIncrementarCounterQuandoLancarException() throws Throwable {
        Object targetObject = new Object();
        when(joinPoint.getTarget()).thenReturn(targetObject);

        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("findAll");
        when(joinPoint.getSignature()).thenReturn(signature);

        when(joinPoint.proceed()).thenThrow(new RuntimeException("Erro de teste"));

        Timer.Sample sample = mock(Timer.Sample.class);
        when(metricsService.startTimer()).thenReturn(sample);

        assertThrows(RuntimeException.class, () -> aspect.monitorRepository(joinPoint));

        verify(metricsService, times(1)).incrementCounter(
                eq("repository_errors"),
                any(String[].class));

        verify(metricsService, times(1)).stopTimer(
                eq(sample),
                eq("repository_method_time"),
                any(String[].class));
    }

    @Test
    void deveMonitorarService_comSucesso() throws Throwable {
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = aspect.monitorService(joinPoint);

        verify(metricsService).stopTimer(eq(sample), eq("service_method_time"),
                any(), any(), any(), any());
        assert result.equals("ok");
    }

    @Test
    void monitorService_deveIncrementarCounterQuandoLancarException() throws Throwable {
        Object targetObject = new Object();
        when(joinPoint.getTarget()).thenReturn(targetObject);

        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("criarVenda");
        when(joinPoint.getSignature()).thenReturn(signature);

        when(joinPoint.proceed()).thenThrow(new RuntimeException("Erro de teste"));

        Timer.Sample sample = mock(Timer.Sample.class);
        when(metricsService.startTimer()).thenReturn(sample);

        assertThrows(RuntimeException.class, () -> aspect.monitorService(joinPoint));

        verify(metricsService, times(1)).incrementCounter(
                eq("service_errors"),
                any(String[].class));

        verify(metricsService, times(1)).stopTimer(
                eq(sample),
                eq("service_method_time"),
                any(String[].class));
    }

    @Test
    void deveMonitorarController_comSucesso() throws Throwable {
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = aspect.monitorController(joinPoint);

        verify(metricsService).stopTimer(eq(sample), eq("controller_method_time"),
                any(), any(), any(), any());
        assert result.equals("ok");
    }

    @Test
    void monitorController_deveIncrementarCounterQuandoLancarException() throws Throwable {
        Object targetObject = new Object();
        when(joinPoint.getTarget()).thenReturn(targetObject);

        Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn("criarVenda");
        when(joinPoint.getSignature()).thenReturn(signature);

        when(joinPoint.proceed()).thenThrow(new RuntimeException("Erro de teste"));

        Timer.Sample sample = mock(Timer.Sample.class);
        when(metricsService.startTimer()).thenReturn(sample);

        assertThrows(RuntimeException.class, () -> aspect.monitorController(joinPoint));

        verify(metricsService, times(1)).incrementCounter(
                eq("controller_errors"),
                any(String[].class));

        verify(metricsService, times(1)).stopTimer(
                eq(sample),
                eq("controller_method_time"),
                any(String[].class));
    }

}
