package com.pragma.franchise.domain.api.franchise;

import com.pragma.franchise.domain.model.Franchise;
import reactor.core.publisher.Mono;

public interface UpdateFranchiseServicePort {

    Mono<Franchise> updateNameFranchise(Long id, String name);
}
