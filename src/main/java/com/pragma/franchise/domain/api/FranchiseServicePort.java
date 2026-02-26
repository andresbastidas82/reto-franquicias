package com.pragma.franchise.domain.api;

import com.pragma.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface FranchiseServicePort {
    Mono<Franchise> createFranchise(Franchise franchise);
}
