package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.branch.CreateBranchServicePort;
import com.pragma.franchise.domain.api.branch.UpdateBranchServicePort;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.branch.CreateBranchUseCase;
import com.pragma.franchise.domain.usecase.branch.UpdateBranchUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.BranchPersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.BranchEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.resilience.ResilienceHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BranchUseCasesConfig {

    @Bean
    public BranchPersistencePort branchPersistencePort(
            BranchRepository repository,
            BranchEntityMapper mapper,
            ResilienceHelper resilienceHelper) {
        return new BranchPersistenceAdapter(repository, mapper, resilienceHelper);
    }

    @Bean
    public CreateBranchServicePort createBranchServicePort(
            BranchPersistencePort branchPersistencePort,
            FranchisePersistencePort franchisePersistencePort) {
        return new CreateBranchUseCase(branchPersistencePort, franchisePersistencePort);
    }

    @Bean
    public UpdateBranchServicePort updateBranchServicePort(
            BranchPersistencePort branchPersistencePort) {
        return new UpdateBranchUseCase(branchPersistencePort);
    }
}
