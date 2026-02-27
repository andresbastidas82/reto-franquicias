package com.pragma.franchise.domain.usecase.franchise;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.NotFoundException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseUseCaseUnitTest {

    @Mock
    private FranchisePersistencePort franchisePersistencePort;

    @InjectMocks
    private UpdateFranchiseUseCase updateFranchiseUseCase;

    private Franchise existingFranchise;

    @BeforeEach
    void setUp() {
        existingFranchise = Franchise.builder()
                .id(1L)
                .name("Old Franchise Name")
                .build();
    }

    @Test
    @DisplayName("Should update franchise name when franchise exists")
    void updateNameFranchise_WhenFranchiseExists_ShouldReturnUpdatedFranchise() {
        // Arrange
        String newName = "New Franchise Name";
        when(franchisePersistencePort.findById(1L))
                .thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Franchise> result = updateFranchiseUseCase.updateNameFranchise(1L, newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(franchise ->
                        franchise.getName().equals("New Franchise Name")
                        && franchise.getUpdatedAt() != null)
                .verifyComplete();

        verify(franchisePersistencePort).findById(1L);
        verify(franchisePersistencePort).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should trim franchise name before saving")
    void updateNameFranchise_WhenNameHasWhitespace_ShouldTrimAndSave() {
        // Arrange
        String nameWithSpaces = "  Trimmed Franchise  ";
        when(franchisePersistencePort.findById(1L))
                .thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Franchise> result = updateFranchiseUseCase.updateNameFranchise(1L, nameWithSpaces);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(franchise ->
                        franchise.getName().equals("Trimmed Franchise"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw NotFoundException when franchise does not exist")
    void updateNameFranchise_WhenFranchiseNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(franchisePersistencePort.findById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Franchise> result = updateFranchiseUseCase.updateNameFranchise(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.FRANCHISE_NOT_FOUND.getMessage()))
                .verify();

        verify(franchisePersistencePort).findById(1L);
        verify(franchisePersistencePort, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("Should propagate error when findById fails")
    void updateNameFranchise_WhenFindByIdFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(franchisePersistencePort.findById(1L))
                .thenReturn(Mono.error(dbError));

        // Act
        Mono<Franchise> result = updateFranchiseUseCase.updateNameFranchise(1L, "New Name");

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
    void updateNameFranchise_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        RuntimeException saveError = new RuntimeException("Save failed");
        when(franchisePersistencePort.findById(1L))
                .thenReturn(Mono.just(existingFranchise));
        when(franchisePersistencePort.save(any(Franchise.class)))
                .thenReturn(Mono.error(saveError));

        // Act
        Mono<Franchise> result = updateFranchiseUseCase.updateNameFranchise(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(franchisePersistencePort).findById(1L);
        verify(franchisePersistencePort).save(any(Franchise.class));
    }
}
