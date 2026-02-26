package com.pragma.franchise.infrastructure.entrypoints.franchise;

import com.pragma.franchise.infrastructure.entrypoints.franchise.handler.FranchiseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRouterFunction(FranchiseHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/franchise", handler::createFranchise)
                .PATCH("/api/v1/franchise/{id}", handler::updateNameFranchise)
                .build();
    }
}
