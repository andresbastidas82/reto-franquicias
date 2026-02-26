package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.FranchiseServicePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.franchise.CreateFranchiseUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseEntityMapper franchiseEntityMapper;

    @Bean
    public FranchisePersistencePort franchisesPersistencePort() {
        return new FranchisePersistenceAdapter(franchiseRepository, franchiseEntityMapper);
    }

    @Bean
    public FranchiseServicePort franchisesServicePort(FranchisePersistencePort franchisePersistencePort){
        return new CreateFranchiseUseCase(franchisePersistencePort);
    }

}
