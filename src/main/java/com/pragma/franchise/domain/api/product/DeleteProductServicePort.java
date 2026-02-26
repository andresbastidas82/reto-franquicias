package com.pragma.franchise.domain.api.product;

import reactor.core.publisher.Mono;

public interface DeleteProductServicePort {

    Mono<Void> deleteProductById(Long productId);
}
