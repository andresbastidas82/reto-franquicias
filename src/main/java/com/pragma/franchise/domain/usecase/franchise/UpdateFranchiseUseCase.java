package com.pragma.franchise.domain.usecase.franchise;

import com.pragma.franchise.domain.api.franchise.UpdateFranchiseServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class UpdateFranchiseUseCase implements UpdateFranchiseServicePort {

    private final FranchisePersistencePort franchisePersistencePort;

    public UpdateFranchiseUseCase(FranchisePersistencePort franchisePersistencePort) {
        this.franchisePersistencePort = franchisePersistencePort;
    }

    @Override
    public Mono<Franchise> updateNameFranchise(Long id, String name) {
        return franchisePersistencePort.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage())))
                .map(franchise -> modifyName(franchise, name))
                .flatMap(franchisePersistencePort::save);
    }

    private Franchise modifyName(Franchise franchise, String name) {
        franchise.setName(name.trim());
        franchise.setUpdatedAt(LocalDateTime.now());
        return franchise;
    }
}
