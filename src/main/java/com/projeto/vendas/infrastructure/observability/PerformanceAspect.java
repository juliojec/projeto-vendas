package com.projeto.vendas.infrastructure.observability;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;

@Aspect
@Component
@RequiredArgsConstructor
public class PerformanceAspect {

    private final PerformanceService metricsService;

    @Around("execution(* com.projeto.vendas.domain.repositories.*.*(..))")
    public Object monitorRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = metricsService.startTimer();
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            metricsService.recordDatabaseQuery(className, methodName, duration);

            if (duration > 100) {
                logSlowQuery(className, methodName, duration);
            }

            return result;
        } catch (Exception e) {
            metricsService.incrementCounter("repository_errors",
                    "repository", className,
                    "method", methodName,
                    "error", e.getClass().getSimpleName());
            throw e;
        } finally {
            metricsService.stopTimer(sample, "repository_method_time",
                    "repository", className,
                    "method", methodName);
        }
    }

    @Around("execution(* com.projeto.vendas.domain.services.*.*(..))")
    public Object monitorService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = metricsService.startTimer();

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            metricsService.incrementCounter("service_errors",
                    "service", className,
                    "method", methodName,
                    "error", e.getClass().getSimpleName());
            throw e;
        } finally {
            metricsService.stopTimer(sample, "service_method_time",
                    "service", className,
                    "method", methodName);
        }
    }

    @Around("execution(* com.projeto.vendas.web.controllers.*.*(..))")
    public Object monitorController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        Timer.Sample sample = metricsService.startTimer();

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            metricsService.incrementCounter("controller_errors",
                    "controller", className,
                    "method", methodName,
                    "error", e.getClass().getSimpleName());
            throw e;
        } finally {
            metricsService.stopTimer(sample, "controller_method_time",
                    "controller", className,
                    "method", methodName);
        }
    }

    private void logSlowQuery(String repository, String method, long duration) {
        System.out.println(String.format(
                "SLOW QUERY DETECTED: %s.%s took %dms",
                repository, method, duration
        ));
    }
}