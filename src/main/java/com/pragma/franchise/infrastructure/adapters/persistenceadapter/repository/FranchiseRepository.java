package com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository;

import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface FranchiseRepository extends ReactiveCrudRepository<FranchiseEntity, Long> {

    Mono<Boolean> existsByName(String name);
}
