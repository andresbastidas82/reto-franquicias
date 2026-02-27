package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.enums.TechnicalMessage;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseUnitTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private DeleteProductUseCase deleteProductUseCase;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100)
                .branchId(1L)
                .build();
    }

    @Test
    @DisplayName("Should delete product when it exists")
    void deleteProductById_WhenProductExists_ShouldComplete() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(product));
        when(productPersistencePort.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteProductUseCase.deleteProductById(1L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(productPersistencePort).findById(1L);
        verify(productPersistencePort).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw NotFoundException when product does not exist")
    void deleteProductById_WhenProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.empty());
        when(productPersistencePort.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteProductUseCase.deleteProductById(1L);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof NotFoundException
                        && throwable.getMessage().equals(TechnicalMessage.PRODUCT_NOT_FOUND.getMessage()))
                .verify();

        verify(productPersistencePort).findById(1L);
    }

    @Test
    @DisplayName("Should propagate error when findById fails")
    void deleteProductById_WhenFindByIdFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.error(dbError));
        when(productPersistencePort.deleteById(1L))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = deleteProductUseCase.deleteProductById(1L);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();
    }

    @Test
    @DisplayName("Should propagate error when deleteById fails")
    void deleteProductById_WhenDeleteFails_ShouldPropagateError() {
        // Arrange
        RuntimeException deleteError = new RuntimeException("Delete failed");
        when(productPersistencePort.findById(1L))
                .thenReturn(Mono.just(product));
        when(productPersistencePort.deleteById(1L))
                .thenReturn(Mono.error(deleteError));

        // Act
        Mono<Void> result = deleteProductUseCase.deleteProductById(1L);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Delete failed"))
                .verify();

        verify(productPersistencePort).findById(1L);
        verify(productPersistencePort).deleteById(1L);
    }
}
