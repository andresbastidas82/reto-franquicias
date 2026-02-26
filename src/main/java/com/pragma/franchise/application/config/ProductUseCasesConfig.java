package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.product.CreateProductServicePort;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import com.pragma.franchise.domain.usecase.product.CreateCreateProductUseCase;
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
    public CreateProductServicePort productServicePort(
            ProductPersistencePort productPersistencePort,
            BranchPersistencePort branchPersistencePort) {

        return new CreateCreateProductUseCase(productPersistencePort, branchPersistencePort);
    }
}
