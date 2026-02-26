package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.api.product.DeleteProductServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Mono;

public class DeleteProductUseCase implements DeleteProductServicePort {

    private final ProductPersistencePort productPersistencePort;

    public DeleteProductUseCase(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public Mono<Void> deleteProductById(Long productId) {
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage())))
                .then(productPersistencePort.deleteById(productId));
    }
}
