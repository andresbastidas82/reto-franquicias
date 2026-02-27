package com.pragma.franchise.domain.usecase.franchise;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.domain.model.Franchise;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseUnitTest {

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private CreateFranchiseUseCase createFranchiseUseCase;

    private Franchise franchise;

    @BeforeEach
    void setUp() {
        franchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }

    @Test
    @DisplayName("Should create franchise when name does not exist")
    void createFranchise_WhenNameDoesNotExist_ShouldReturnSavedFranchise() {
        // Arrange
        when(franchisePersistencePort.existByName(franchise.getName()))
                .thenReturn(Mono.just(false));
        when(franchisePersistencePort.save(franchise))
                .thenReturn(Mono.just(franchise));

        // Act
        Mono<Franchise> result = createFranchiseUseCase.createFranchise(franchise);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.getId().equals(1L)
                        && saved.getName().equals("Test Franchise"))
                .verifyComplete();

        verify(franchisePersistencePort).existByName(franchise.getName());
        verify(franchisePersistencePort).save(franchise);
    }

    @Test
    @DisplayName("Should throw BusinessException when franchise name already exists")
    void createFranchise_WhenNameAlreadyExists_ShouldThrowBusinessException() {
        // Arrange
        when(franchisePersistencePort.existByName(franchise.getName()))
                .thenReturn(Mono.just(true));

        // Act
        Mono<Franchise> result = createFranchiseUseCase.createFranchise(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException
                        && throwable.getMessage().equals(TechnicalMessage.FRANCHISE_ALREADY_EXISTS.getMessage()))
                .verify();

        verify(franchisePersistencePort).existByName(franchise.getName());
        verify(franchisePersistencePort, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should propagate error when existByName fails")
    void createFranchise_WhenExistByNameFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(franchisePersistencePort.existByName(anyString()))
                .thenReturn(Mono.error(dbError));

        // Act
        Mono<Franchise> result = createFranchiseUseCase.createFranchise(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(franchisePersistencePort, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should propagate error when save fails")
    void createFranchise_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        RuntimeException saveError = new RuntimeException("Save failed");
        when(franchisePersistencePort.existByName(franchise.getName()))
                .thenReturn(Mono.just(false));
        when(franchisePersistencePort.save(franchise))
                .thenReturn(Mono.error(saveError));

        // Act
        Mono<Franchise> result = createFranchiseUseCase.createFranchise(franchise);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(franchisePersistencePort).existByName(franchise.getName());
        verify(franchisePersistencePort).save(franchise);
    }
}
