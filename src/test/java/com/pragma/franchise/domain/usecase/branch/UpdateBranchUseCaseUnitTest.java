package com.pragma.franchise.domain.usecase.branch;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateBranchUseCaseUnitTest {

    @Mock
    private BranchPersistencePort branchPersistencePort;

    @InjectMocks
    private UpdateBranchUseCase updateBranchUseCase;

    private Branch existingBranch;

    @BeforeEach
    void setUp() {
        existingBranch = Branch.builder()
                .id(1L)
                .name("Old Branch Name")
                .franchiseId(1L)
                .build();
    }

    @Test
    @DisplayName("Should update branch name when branch exists and name is valid")
    void updateNameBranch_WhenBranchExistsAndNameValid_ShouldReturnUpdatedBranch() {
        // Arrange
        String newName = "New Branch Name";
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingBranch));
        when(branchPersistencePort.save(any(Branch.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, newName);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(branch ->
                        branch.getName().equals("New Branch Name")
                        && branch.getUpdatedAt() != null)
                .verifyComplete();

        verify(branchPersistencePort).findById(1L);
        verify(branchPersistencePort).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should trim branch name before saving")
    void updateNameBranch_WhenNameHasWhitespace_ShouldTrimAndSave() {
        // Arrange
        String nameWithSpaces = "  Trimmed Name  ";
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingBranch));
        when(branchPersistencePort.save(any(Branch.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, nameWithSpaces);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(branch ->
                        branch.getName().equals("Trimmed Name"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw NotFoundException when branch does not exist")
    void updateNameBranch_WhenBranchNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.BRANCH_NOT_FOUND.getMessage()))
                .verify();

        verify(branchPersistencePort).findById(1L);
        verify(branchPersistencePort, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when name is null")
    void updateNameBranch_WhenNameIsNull_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed, validation happens before any port call

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, null);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_REQUEST.getMessage()))
                .verify();

        verify(branchPersistencePort, never()).findById(anyLong());
        verify(branchPersistencePort, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when name is empty")
    void updateNameBranch_WhenNameIsEmpty_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed, validation happens before any port call

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, "");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_REQUEST.getMessage()))
                .verify();

        verify(branchPersistencePort, never()).findById(anyLong());
        verify(branchPersistencePort, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should propagate error when findById fails")
    void updateNameBranch_WhenFindByIdFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.error(dbError));

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(branchPersistencePort, never()).save(any(Branch.class));
    }

    @Test
    @DisplayName("Should propagate error when save fails")
    void updateNameBranch_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        RuntimeException saveError = new RuntimeException("Save failed");
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingBranch));
        when(branchPersistencePort.save(any(Branch.class)))
                .thenReturn(Mono.error(saveError));

        // Act
        Mono<Branch> result = updateBranchUseCase.updateNameBranch(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(branchPersistencePort).findById(1L);
        verify(branchPersistencePort).save(any(Branch.class));
    }
}
