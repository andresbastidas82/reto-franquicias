package com.pragma.franchise.infrastructure.adapters.persistenceadapter;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.ProductEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ProductPersistenceAdapter implements ProductPersistencePort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    public ProductPersistenceAdapter(ProductRepository productRepository, ProductEntityMapper productEntityMapper) {
        this.productRepository = productRepository;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public Mono<Product> save(Product product) {
        return productRepository.save(productEntityMapper.toEntity(product))
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existsByNameAndBranchId(String name, Long branchId) {
        return productRepository.existsByNameAndBranchId(name, branchId);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productRepository.findById(id)
                .map(productEntityMapper::toModel);
    }

    @Override
    public Mono<Void> deleteById(Long productId) {
        return productRepository.deleteById(productId);
    }

    @Override
    public Flux<TopStockProduct> getTopStockProducts(Long franchiseId) {
        return productRepository.findTopStockProductsByFranchise(franchiseId);
    }

}
