package com.pragma.franchise.application.config;

import com.pragma.franchise.domain.api.franchise.CreateFranchiseServicePort;
import com.pragma.franchise.domain.api.franchise.UpdateFranchiseServicePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import com.pragma.franchise.domain.usecase.franchise.CreateFranchiseUseCase;
import com.pragma.franchise.domain.usecase.franchise.UpdateFranchiseUseCase;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.FranchisePersistenceAdapter;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper.FranchiseEntityMapper;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.repository.FranchiseRepository;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.resilience.ResilienceHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FranchiseUseCasesConfig {

    @Bean
    public FranchisePersistencePort franchisePersistencePort(
            FranchiseRepository repository,
            FranchiseEntityMapper mapper,
            ResilienceHelper resilienceHelper) {
        return new FranchisePersistenceAdapter(repository, mapper, resilienceHelper);
    }

    @Bean
    public CreateFranchiseServicePort createFranchisesServicePort(
            FranchisePersistencePort franchisePersistencePort){
        return new CreateFranchiseUseCase(franchisePersistencePort);
    }

    @Bean
    public UpdateFranchiseServicePort updateFranchisesServicePort(
            FranchisePersistencePort franchisePersistencePort){
        return new UpdateFranchiseUseCase(franchisePersistencePort);
    }
}
