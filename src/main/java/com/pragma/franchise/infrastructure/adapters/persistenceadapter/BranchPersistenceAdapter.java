package com.pragma.franchise.infrastructure.adapters.persistenceadapter;

import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.BranchEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class BranchPersistenceAdapter implements BranchPersistencePort {

    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;


    @Override
    public Mono<Branch> save(Branch branch) {
        return branchRepository.save(branchEntityMapper.toEntity(branch))
                .map(branchEntityMapper::toModel);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return branchRepository.findById(id)
                .map(branchEntityMapper::toModel);
    }
}
