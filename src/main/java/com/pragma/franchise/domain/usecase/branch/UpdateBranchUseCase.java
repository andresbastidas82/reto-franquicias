package com.pragma.franchise.domain.usecase.branch;

import com.pragma.franchise.domain.api.branch.UpdateBranchServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class UpdateBranchUseCase implements UpdateBranchServicePort {

    private final BranchPersistencePort branchPersistencePort;

    public UpdateBranchUseCase(BranchPersistencePort branchPersistencePort) {
        this.branchPersistencePort = branchPersistencePort;
    }

    @Override
    public Mono<Branch> updateNameBranch(Long id, String name) {
        if(name == null || name.isEmpty()) {
            return Mono.error(new BadRequestException(TechnicalMessage.INVALID_REQUEST.getMessage()));
        }
        return branchPersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.BRANCH_NOT_FOUND.getMessage())))
                .map(branch -> modifyName(branch, name))
                .flatMap(branchPersistencePort::save);
    }

    private Branch modifyName(Branch branch, String name) {
        branch.setName(name.trim());
        branch.setUpdatedAt(LocalDateTime.now());
        return branch;
    }
}
