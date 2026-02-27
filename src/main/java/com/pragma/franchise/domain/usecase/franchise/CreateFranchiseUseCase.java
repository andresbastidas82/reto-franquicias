package com.pragma.franchise.domain.usecase.franchise;

import com.pragma.franchise.domain.api.franchise.CreateFranchiseServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import reactor.core.publisher.Mono;

public class CreateFranchiseUseCase implements CreateFranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;

    public CreateFranchiseUseCase(FranchisePersistencePort franchisePersistencePort) {
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchisePersistencePort.existByName(franchise.getName())
                .flatMap(exist -> Boolean.TRUE.equals(exist)
                        ? Mono.error(new BusinessException(TechnicalMessage.FRANCHISE_ALREADY_EXISTS))
                        : franchisePersistencePort.save(franchise));
    }
}
