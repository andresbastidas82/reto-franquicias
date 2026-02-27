package com.pragma.franchise.domain.usecase.branch;

import com.pragma.franchise.domain.api.branch.CreateBranchServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import reactor.core.publisher.Mono;

public class CreateBranchUseCase implements CreateBranchServicePort {

    private final BranchPersistencePort branchPersistencePort;
    private final FranchisePersistencePort franchisePersistencePort;

    public CreateBranchUseCase(BranchPersistencePort branchPersistencePort,
                               FranchisePersistencePort franchisePersistencePort) {
        this.branchPersistencePort = branchPersistencePort;
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Branch> createBranch(Branch branch) {
        return franchisePersistencePort.findById(branch.getFranchiseId())
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage())))
                .then(branchPersistencePort.save(branch));
    }
}
