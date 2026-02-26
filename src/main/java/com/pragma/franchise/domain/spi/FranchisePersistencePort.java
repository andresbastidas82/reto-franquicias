package com.pragma.franchise.domain.spi;

import com.pragma.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchisePersistencePort {

    Mono<Franchise> save(Franchise franchise);
    Mono<Boolean> existByName(String name);
}
