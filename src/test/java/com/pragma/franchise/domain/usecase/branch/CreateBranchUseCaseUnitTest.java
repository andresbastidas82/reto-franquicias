package com.pragma.franchise.domain.usecase.branch;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.FranchisePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateBranchUseCaseUnitTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private CreateBranchUseCase createBranchUseCase;

    private Branch branch;
    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();
    }

    @Test
    @DisplayName("Debe crear una sucursal cuando la franquicia existe")
    void createBranch_WhenFranchiseExists_ShouldReturnSavedBranch() {
        // Arrange
        when(franchisePersistencePort.findById(branch.getFranchiseId()))
                .thenReturn(Mono.just(franchise));
        when(branchPersistencePort.save(branch))
                .thenReturn(Mono.just(branch));

        // Act
        Mono<Branch> result = createBranchUseCase.createBranch(branch);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedBranch ->
                        savedBranch.getId().equals(1L)
                        && savedBranch.getName().equals("Test Branch")
                        && savedBranch.getFranchiseId().equals(1L))
                .verifyComplete();

        verify(franchisePersistencePort).findById(branch.getFranchiseId());
        verify(branchPersistencePort).save(branch);
    }

    @Test
    @DisplayName("Debe lanzar NotFoundException cuando la franquicia no existe")
    void createBranch_WhenFranchiseNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(franchisePersistencePort.findById(branch.getFranchiseId()))
                .thenReturn(Mono.empty());
        when(branchPersistencePort.save(any(Branch.class)))
                .thenReturn(Mono.just(branch));

        // Act
        Mono<Branch> result = createBranchUseCase.createBranch(branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage()))
                .verify();

        verify(franchisePersistencePort).findById(branch.getFranchiseId());
    }

    @Test
    @DisplayName("Debe propagar error cuando findById falla")
    void createBranch_WhenFindByIdFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database connection error");
        when(franchisePersistencePort.findById(anyLong()))
                .thenReturn(Mono.error(dbError));
        when(branchPersistencePort.save(any(Branch.class)))
                .thenReturn(Mono.just(branch));

        // Act
        Mono<Branch> result = createBranchUseCase.createBranch(branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database connection error"))
                .verify();

        verify(franchisePersistencePort).findById(branch.getFranchiseId());
    }

    @Test
    @DisplayName("Debe propagar error cuando save falla")
    void createBranch_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        RuntimeException saveError = new RuntimeException("Save failed");
        when(franchisePersistencePort.findById(branch.getFranchiseId()))
                .thenReturn(Mono.just(franchise));
        when(branchPersistencePort.save(branch))
                .thenReturn(Mono.error(saveError));

        // Act
        Mono<Branch> result = createBranchUseCase.createBranch(branch);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(franchisePersistencePort).findById(branch.getFranchiseId());
        verify(branchPersistencePort).save(branch);
    }
}
