package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.domain.model.Product;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductUseCaseUnitTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private UpdateProductUseCase updateProductUseCase;

    private Product existingProduct;

    @BeforeEach
    void setUp() {
        existingProduct = Product.builder()
                .id(1L)
                .name("Old Product")
                .stock(50)
                .branchId(1L)
                .build();
    }

    // ==================== updateStockProduct ====================

    @Test
    @DisplayName("Should update stock when product exists and stock is valid")
    void updateStockProduct_WhenProductExistsAndStockValid_ShouldReturnUpdated() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, 200);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(product ->
                        product.getStock().equals(200)
                        && product.getUpdatedAt() != null)
                .verifyComplete();

        verify(productPersistencePort).findById(1L);
        verify(productPersistencePort).save(any(Product.class));
    }

    @Test
    @DisplayName("Should allow setting stock to zero")
    void updateStockProduct_WhenStockIsZero_ShouldReturnUpdated() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, 0);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(product -> product.getStock().equals(0))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BadRequestException when stock is null")
    void updateStockProduct_WhenStockIsNull_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, null);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_PARAMETERS.getMessage()))
                .verify();

        verify(productPersistencePort, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw BadRequestException when stock is negative")
    void updateStockProduct_WhenStockIsNegative_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, -5);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_PARAMETERS.getMessage()))
                .verify();

        verify(productPersistencePort, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found for stock update")
    void updateStockProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, 100);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage()))
                .verify();

        verify(productPersistencePort, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should propagate error when save fails on stock update")
    void updateStockProduct_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // Act
        Mono<Product> result = updateProductUseCase.updateStockProduct(1L, 100);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();
    }

    // ==================== updateNameProduct ====================

    @Test
    @DisplayName("Should update name when product exists and name is valid")
    void updateNameProduct_WhenProductExistsAndNameValid_ShouldReturnUpdated() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, "New Product Name");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(product ->
                        product.getName().equals("New Product Name")
                        && product.getUpdatedAt() != null)
                .verifyComplete();

        verify(productPersistencePort).findById(1L);
        verify(productPersistencePort).save(any(Product.class));
    }

    @Test
    @DisplayName("Should trim product name before saving")
    void updateNameProduct_WhenNameHasWhitespace_ShouldTrimAndSave() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, "  Trimmed Name  ");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(product -> product.getName().equals("Trimmed Name"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw BadRequestException when name is null")
    void updateNameProduct_WhenNameIsNull_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, null);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_PARAMETERS.getMessage()))
                .verify();

        verify(productPersistencePort, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw BadRequestException when name is empty")
    void updateNameProduct_WhenNameIsEmpty_ShouldThrowBadRequestException() {
        // Arrange — no mocks needed

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, "");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof BadRequestException
                        && throwable.getMessage().equals(TechnicalMessage.INVALID_PARAMETERS.getMessage()))
                .verify();

        verify(productPersistencePort, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should throw NotFoundException when product not found for name update")
    void updateNameProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage()))
                .verify();

        verify(productPersistencePort, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should propagate error when save fails on name update")
    void updateNameProduct_WhenSaveFails_ShouldPropagateError() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(existingProduct));
        when(productPersistencePort.save(any(Product.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        // Act
        Mono<Product> result = updateProductUseCase.updateNameProduct(1L, "New Name");

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Save failed"))
                .verify();
    }
}
