package com.pragma.franchise.infrastructure.adapters.persistenceadapter.resilience;

import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Function;

@AllArgsConstructor
public class ResilienceHelper {

    private static final Duration TIMEOUT = Duration.ofSeconds(2);
    private static final String CIRCUIT_BREAKER_NAME = "databaseCB";
    private static final String BULKHEAD_NAME = "databaseBH";

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    public <T> Mono<T> applyResilience(Mono<T> mono) {
        return mono
                .timeout(TIMEOUT)
                .transformDeferred(BulkheadOperator.of(bulkheadRegistry.bulkhead(BULKHEAD_NAME)))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME)));
    }

    public <T> Flux<T> applyResilience(Flux<T> flux) {
        return flux
                .timeout(TIMEOUT)
                .transformDeferred(BulkheadOperator.of(bulkheadRegistry.bulkhead(BULKHEAD_NAME)))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME)));
    }
}
