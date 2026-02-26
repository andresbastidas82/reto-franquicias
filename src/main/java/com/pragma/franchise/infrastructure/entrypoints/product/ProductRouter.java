package com.pragma.franchise.infrastructure.entrypoints.product;

import com.pragma.franchise.infrastructure.entrypoints.product.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productsRouter(ProductHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/products", handler::createProduct)
                .build();
    }
}
