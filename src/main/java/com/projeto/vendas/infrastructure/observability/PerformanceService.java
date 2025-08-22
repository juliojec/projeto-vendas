package com.projeto.vendas.infrastructure.observability;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PerformanceService {

    private final MeterRegistry meterRegistry;
    private final Map<String, Timer> timers = new ConcurrentHashMap<>();
    private final Map<String, Counter> counters = new ConcurrentHashMap<>();

    public PerformanceService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String metricName, String... tags) {
        Timer timer = getOrCreateTimer(metricName, tags);
        sample.stop(timer);
    }

    public void incrementCounter(String metricName, String... tags) {
        Counter counter = getOrCreateCounter(metricName, tags);
        counter.increment();
    }

    public void recordDatabaseQuery(String table, String operation, long durationMs) {
        Timer timer = getOrCreateTimer("database_query_time",
                "table", table,
                "operation", operation);
        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    private Timer getOrCreateTimer(String name, String... tags) {
        String key = buildKey(name, tags);
        return timers.computeIfAbsent(key, k ->
                Timer.builder(name)
                    .tags(tags)
                    .description("Performance metric: " + name)
                    .register(meterRegistry));
    }

    private Counter getOrCreateCounter(String name, String... tags) {
        String key = buildKey(name, tags);
        return counters.computeIfAbsent(key, k ->
                Counter.builder(name)
                    .tags(tags)
                    .description("Error counter: " + name)
                    .register(meterRegistry));
    }

    private String buildKey(String name, String... tags) {
        if (tags.length == 0) {
            return name;
        }
        return name + ":" + String.join(":", tags);
    }
}