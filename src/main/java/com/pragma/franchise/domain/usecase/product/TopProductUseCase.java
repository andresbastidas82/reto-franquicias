package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.api.product.TopProductServicePort;
import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Flux;

public class TopProductUseCase implements TopProductServicePort {

    private final ProductPersistencePort productPersistencePort;

    public TopProductUseCase(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public Flux<TopStockProduct> getTopStockProducts(Long franchiseId) {
        return productPersistencePort.getTopStockProducts(franchiseId);
    }
}
