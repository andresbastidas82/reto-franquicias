package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.spi.BranchPersistencePort;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseUnitTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @Mock
    private BranchPersistencePort branchPersistencePort;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    private Product product;
    private Branch branch;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(1L)
                .name("Test Branch")
                .franchiseId(1L)
                .build();

        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .build();
    }

    @Test
    @DisplayName("Should create product when branch exists and product name is unique")
    void createProduct_WhenBranchExistsAndNameUnique_ShouldReturnSavedProduct() {
        // Arrange
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Test Product", 1L))
                .thenReturn(Mono.just(false));
        when(productPersistencePort.save(product))
                .thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(saved ->
                        saved.getId().equals(1L)
                        && saved.getName().equals("Test Product")
                        && saved.getStock().equals(100)
                        && saved.getBranchId().equals(1L))
                .verifyComplete();

        verify(branchPersistencePort).findById(1L);
        verify(productPersistencePort).existsByNameAndBranchId("Test Product", 1L);
        verify(productPersistencePort).save(product);
    }

    @Test
    @DisplayName("Should throw NotFoundException when branch does not exist")
    void createProduct_WhenBranchNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.empty());
        when(productPersistencePort.existsByNameAndBranchId(anyString(), anyLong()))
                .thenReturn(Mono.just(false));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.BRANCH_NOT_FOUND.getMessage()))
                .verify();

        verify(branchPersistencePort).findById(1L);
    }

    @Test
    @DisplayName("Should throw BusinessException when product name already exists in branch")
    void createProduct_WhenProductAlreadyExists_ShouldThrowBusinessException() {
        // Arrange
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Test Product", 1L))
                .thenReturn(Mono.just(true));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BusinessException
                        && throwable.getMessage().equals(TechnicalMessage.PRODUCT_ALREADY_EXISTS.getMessage()))
                .verify();

        verify(branchPersistencePort).findById(1L);
        verify(productPersistencePort).existsByNameAndBranchId("Test Product", 1L);
    }

    @Test
    @DisplayName("Should propagate error when findById fails")
    void createProduct_WhenFindByIdFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.error(dbError));
        when(productPersistencePort.existsByNameAndBranchId(anyString(), anyLong()))
                .thenReturn(Mono.just(false));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when existsByNameAndBranchId fails")
    void createProduct_WhenExistsByNameFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Query error");
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Test Product", 1L))
                .thenReturn(Mono.error(dbError));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Query error"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when save fails")
    void createProduct_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        RuntimeException saveError = new RuntimeException("Save failed");
        when(branchPersistencePort.findById(1L))
                .thenReturn(Mono.just(branch));
        when(productPersistencePort.existsByNameAndBranchId("Test Product", 1L))
                .thenReturn(Mono.just(false));
        when(productPersistencePort.save(product))
                .thenReturn(Mono.error(saveError));

        // Act
        Mono<Product> result = createProductUseCase.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();

        verify(branchPersistencePort).findById(1L);
        verify(productPersistencePort).existsByNameAndBranchId("Test Product", 1L);
        verify(productPersistencePort).save(product);
    }
}
