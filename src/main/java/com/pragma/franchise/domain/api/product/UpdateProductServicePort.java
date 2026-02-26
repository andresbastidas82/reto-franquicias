package com.pragma.franchise.domain.api.product;

import com.pragma.franchise.domain.model.Product;
import reactor.core.publisher.Mono;

public interface UpdateProductServicePort {

    Mono<Product> updateStockProduct(Long productId, Integer stock);

    Mono<Product> updateNameProduct(Long productId, String name);

}
