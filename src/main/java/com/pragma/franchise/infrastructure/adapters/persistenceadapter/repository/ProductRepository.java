package com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<Boolean> existsByNameAndBranchId(String name, Long branchId);
}
