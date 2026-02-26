package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.BranchServicePort;
import com.pragma.franchise.domain.api.FranchiseServicePort;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.branch.CreateBranchUseCase;
import com.pragma.franchise.domain.usecase.franchise.CreateFranchiseUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.BranchPersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.BranchEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.BranchRepository;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;

    private final BranchRepository branchRepository;
    private final BranchEntityMapper branchEntityMapper;

    @Bean
    public FranchisePersistencePort franchisesPersistencePort() {
        return new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper);
    }

    @Bean
    public FranchiseServicePort franchisesServicePort(FranchisePersistencePort franchisePersistencePort){
        return new CreateFranchiseUseCase(franchisePersistencePort);
    }

    @Bean
    public BranchPersistencePort branchesPersistencePort() {
        return new BranchPersistenceAdapter(branchRepository, branchEntityMapper);
    }

    @Bean
    public BranchServicePort branchesServicePort(BranchPersistencePort branchPersistencePort,
                                                 FranchisePersistencePort franchisePersistencePort){
        return new CreateBranchUseCase(branchPersistencePort, franchisePersistencePort);
    }

}
