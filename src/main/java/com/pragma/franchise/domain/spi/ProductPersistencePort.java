package com.pragma.franchise.domain.spi;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.model.TopStockProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductPersistencePort {

    Mono<Product> save(Product product);

    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);

    Mono<Product> findById(Long id);

    Mono<Void> deleteById(Long productId);

    Flux<TopStockProduct> getTopStockProducts(Long franchiseId);
}
