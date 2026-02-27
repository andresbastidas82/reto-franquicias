package com.pragma.franchise.domain.api.branch;

import com.pragma.franchise.domain.model.Branch;
import reactor.core.publisher.Mono;

public interface CreateBranchServicePort {

    Mono<Branch> createBranch(Branch branch);
}
