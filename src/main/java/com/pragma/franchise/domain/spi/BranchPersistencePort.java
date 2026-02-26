package com.pragma.franchise.domain.spi;

import com.pragma.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface BranchPersistencePort {

    Mono<Branch> save(Branch branch);
}
