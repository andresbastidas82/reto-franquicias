package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.api.product.UpdateProductServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class UpdateProductUseCase implements UpdateProductServicePort {

    private final ProductPersistencePort productPersistencePort;

    public UpdateProductUseCase(ProductPersistencePort productPersistencePort) {
        this.productPersistencePort = productPersistencePort;
    }

    @Override
    public Mono<Product> updateStockProduct(Long productId, Integer stock) {
        if (stock == null || stock < 0) {
            return Mono.error(new BadRequestException(TechnicalMessage.INVALID_PARAMETERS.getMessage()));
        }
        return productPersistencePort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage())))
                .map(product -> modifyStock(product, stock))
                .flatMap(productPersistencePort::save);
    }

    @Override
    public Mono<Product> updateNameProduct(Long productId, String name) {
        if (name == null || name.isEmpty()) {
            return Mono.error(new BadRequestException(TechnicalMessage.INVALID_PARAMETERS.getMessage()));
        }
        return productPersistencePort
                .findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage())))
                .map(product -> modifyName(product, name))
                .flatMap(productPersistencePort::save);
    }

    private Product modifyStock(Product product, Integer stock) {
        product.setStock(stock);
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private Product modifyName(Product product, String name) {
        product.setName(name.trim());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

}
