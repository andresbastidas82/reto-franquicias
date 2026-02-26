package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.api.product.CreateProductServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import reactor.core.publisher.Mono;

public class CreateCreateProductUseCase implements CreateProductServicePort {
    private final ProductPersistencePort productPersistencePort;
    private final BranchPersistencePort branchPersistencePort;

    public CreateCreateProductUseCase(ProductPersistencePort productPersistencePort, BranchPersistencePort branchPersistencePort) {
        this.productPersistencePort = productPersistencePort;
        this.branchPersistencePort = branchPersistencePort;
    }

    @Override
    public Mono<Product> createProduct(Product product) {
        return validateBranchExists(product.getBranchId())
                .then(validateProductDoesNotExist(product))
                .then(productPersistencePort.save(product));
    }

    private Mono<Void> validateBranchExists(Long branchId) {
        return branchPersistencePort.findById(branchId)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.BRANCH_NOT_FOUND.getMessage())))
                .then();
    }

    private Mono<Void> validateProductDoesNotExist(Product product) {
        return productPersistencePort
                .existsByNameAndBranchId(product.getName(), product.getBranchId())
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                                ? Mono.error(new BusinessException(TechnicalMessage.PRODUCT_ALREADY_EXISTS))
                                : Mono.empty()
                );
    }
}
