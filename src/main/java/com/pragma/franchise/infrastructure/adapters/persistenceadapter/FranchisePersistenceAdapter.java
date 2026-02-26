package com.pragma.franchise.infrastructure.adapters.persistenceadapter;

import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class FranchisePersistenceAdapter implements FranchisePersistencePort {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return franchiseRepository
                .save(franchiseEntityMapper.toEntity(franchise))
                .map(franchiseEntityMapper::toModel);
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return franchiseRepository.existsByName(name);
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return franchiseRepository.findById(id)
                .map(franchiseEntityMapper::toModel);
    }
}
