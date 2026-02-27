package com.pragma.franchise.domain.api.product;

import com.pragma.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface CreateProductServicePort {
    Mono<Product> createProduct(Product product);
}
