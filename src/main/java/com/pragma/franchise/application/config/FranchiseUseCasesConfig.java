package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.franchise.CreateFranchiseServicePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.franchise.CreateCreateFranchiseUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FranchiseUseCasesConfig {

    @Bean
    public FranchisePersistencePort franchisePersistencePort(
            FranchiseRepository repository,
            FranchiseEntityMapper mapper) {
        return new FranchisePersistenceAdapter(repository, mapper);
    }

    @Bean
    public CreateFranchiseServicePort franchisesServicePort(
            FranchisePersistencePort franchisePersistencePort){
        return new CreateCreateFranchiseUseCase(franchisePersistencePort);
    }
}
