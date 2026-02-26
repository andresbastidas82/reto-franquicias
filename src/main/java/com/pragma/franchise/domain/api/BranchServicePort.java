package com.pragma.franchise.domain.api;

import com.pragma.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface BranchServicePort {

    Mono<Branch> createBranch(Branch branch);
}
