package com.pragma.franchise.domain.usecase.product;

import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.domain.spi.ProductPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopProductUseCaseUnitTest {

    @Mock
    private ProductPersistencePort productPersistencePort;

    @InjectMocks
    private TopProductUseCase topProductUseCase;

    @Test
    @DisplayName("Should return top stock products for a franchise")
    void getTopStockProducts_WhenProductsExist_ShouldReturnProducts() {
        // Arrange
        TopStockProduct product1 = TopStockProduct.builder()
                .branchId(1L).branchName("Branch A")
                .productId(1L).productName("Product A").stock(200)
                .build();
        TopStockProduct product2 = TopStockProduct.builder()
                .branchId(2L).branchName("Branch B")
                .productId(2L).productName("Product B").stock(150)
                .build();

        when(productPersistencePort.getTopStockProducts(1L))
                .thenReturn(Flux.just(product1, product2));

        // Act
        Flux<TopStockProduct> result = topProductUseCase.getTopStockProducts(1L);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(p -> p.getProductName().equals("Product A") && p.getStock().equals(200))
                .expectNextMatches(p -> p.getProductName().equals("Product B") && p.getStock().equals(150))
                .verifyComplete();

        verify(productPersistencePort).getTopStockProducts(1L);
    }

    @Test
    @DisplayName("Should return empty when no products found for franchise")
    void getTopStockProducts_WhenNoProducts_ShouldReturnEmpty() {
        // Arrange
        when(productPersistencePort.getTopStockProducts(1L))
                .thenReturn(Flux.empty());

        // Act
        Flux<TopStockProduct> result = topProductUseCase.getTopStockProducts(1L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(productPersistencePort).getTopStockProducts(1L);
    }

    @Test
    @DisplayName("Should propagate error when persistence port fails")
    void getTopStockProducts_WhenPersistenceFails_ShouldPropagateError() {
        // Arrange
        RuntimeException dbError = new RuntimeException("Database error");
        when(productPersistencePort.getTopStockProducts(1L))
                .thenReturn(Flux.error(dbError));

        // Act
        Flux<TopStockProduct> result = topProductUseCase.getTopStockProducts(1L);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException
                        && throwable.getMessage().equals("Database error"))
                .verify();

        verify(productPersistencePort).getTopStockProducts(1L);
    }
}
