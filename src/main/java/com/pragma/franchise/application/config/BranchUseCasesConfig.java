package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.branch.CreateBranchServicePort;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.branch.CreateBranchUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.BranchPersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.BranchEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BranchUseCasesConfig {

    @Bean
    public BranchPersistencePort branchPersistencePort(
            BranchRepository repository,
            BranchEntityMapper mapper) {
        return new BranchPersistenceAdapter(repository, mapper);
    }

    @Bean
    public CreateBranchServicePort branchServicePort(
            BranchPersistencePort branchPersistencePort,
            FranchisePersistencePort franchisePersistencePort) {
        return new CreateBranchUseCase(branchPersistencePort, franchisePersistencePort);
    }
}
