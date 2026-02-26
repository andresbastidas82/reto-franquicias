package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.product.CreateProductServicePort;
import com.pragma.franchise.domain.api.product.DeleteProductServicePort;
import com.pragma.franchise.domain.api.product.UpdateProductServicePort;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import com.pragma.franchise.domain.usecase.product.CreateProductUseCase;
import com.pragma.franchise.domain.usecase.product.DeleteProductUseCase;
import com.pragma.franchise.domain.usecase.product.UpdateProductUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.ProductPersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.ProductEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductUseCasesConfig {

    @Bean
    public ProductPersistencePort productPersistencePort(
            ProductRepository repository,
            ProductEntityMapper mapper) {
        return new ProductPersistenceAdapter(repository, mapper);
    }

    @Bean
    public CreateProductServicePort createProductServicePort(
            ProductPersistencePort productPersistencePort,
            BranchPersistencePort branchPersistencePort) {
        return new CreateProductUseCase(productPersistencePort, branchPersistencePort);
    }

    @Bean
    public DeleteProductServicePort deleteProductServicePort(
            ProductPersistencePort productPersistencePort) {
        return new DeleteProductUseCase(productPersistencePort);
    }

    @Bean
    public UpdateProductServicePort updateProductServicePort(
            ProductPersistencePort productPersistencePort) {
        return new UpdateProductUseCase(productPersistencePort);
    }
}
