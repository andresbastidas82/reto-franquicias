package com.pragma.franchise.application.config;

import com.pragma.franchise.infrastructure.adapters.persistenceadapter.resilience.ResilienceHelper;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResilienceConfig {

    @Bean
    public ResilienceHelper resilienceHelper(
            CircuitBreakerRegistry circuitBreakerRegistry,
            BulkheadRegistry bulkheadRegistry) {
        return new ResilienceHelper(circuitBreakerRegistry, bulkheadRegistry);
    }
}
