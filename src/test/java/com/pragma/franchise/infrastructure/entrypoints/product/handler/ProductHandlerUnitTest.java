package com.pragma.franchise.infrastructure.entrypoints.product.handler;

import com.pragma.franchise.domain.api.product.CreateProductServicePort;
import com.pragma.franchise.domain.api.product.DeleteProductServicePort;
import com.pragma.franchise.domain.api.product.TopProductServicePort;
import com.pragma.franchise.domain.api.product.UpdateProductServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.infrastructure.entrypoints.product.ProductRouter;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductResponseDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.TopStockProductResponseDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.pragma.franchise.infrastructure.utils.ValidatorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductHandlerUnitTest {

    @Mock
    private CreateProductServicePort createProductServicePort;
    @Mock
    private DeleteProductServicePort deleteProductServicePort;
    @Mock
    private UpdateProductServicePort updateProductServicePort;
    @Mock
    private TopProductServicePort topProductServicePort;
    @Mock
    private ProductMapper mapper;
    @Mock
    private ValidatorHelper validator;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ProductHandler handler = new ProductHandler(
                createProductServicePort, deleteProductServicePort,
                updateProductServicePort, topProductServicePort,
                mapper, validator);
        ProductRouter router = new ProductRouter();
        webTestClient = WebTestClient
                .bindToRouterFunction(router.productsRouter(handler))
                .build();
    }

    // ==================== createProduct ====================

    @Test
    @DisplayName("Should return 201 when product is created successfully")
    void createProduct_WhenValid_ShouldReturn201() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("Product A", 100, 1L);
        Product product = Product.builder().id(1L).name("Product A").stock(100).branchId(1L).build();
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Product A", 100, 1L);

        when(validator.validate(any(ProductRequestDTO.class)))
                .thenReturn(Mono.just(requestDTO));
        when(mapper.toModel(requestDTO)).thenReturn(product);
        when(createProductServicePort.createProduct(product))
                .thenReturn(Mono.just(product));
        when(mapper.toResponseDto(product)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/products")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.PRODUCT_CREATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_CREATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Product A");
    }

    // ==================== deleteProduct ====================

    @Test
    @DisplayName("Should return 204 when product is deleted successfully")
    void deleteProduct_WhenProductExists_ShouldReturn204() {
        // Arrange
        when(deleteProductServicePort.deleteProductById(1L))
                .thenReturn(Mono.empty());

        // Act & Assert
        webTestClient.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus().isNoContent();
    }

    // ==================== updateStockProduct ====================

    @Test
    @DisplayName("Should return 200 when stock is updated successfully")
    void updateStockProduct_WhenValid_ShouldReturn200() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("Product A", 200, 1L);
        Product updatedProduct = Product.builder().id(1L).name("Product A").stock(200).branchId(1L).build();
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Product A", 200, 1L);

        when(updateProductServicePort.updateStockProduct(1L, 200))
                .thenReturn(Mono.just(updatedProduct));
        when(mapper.toResponseDto(updatedProduct)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/v1/products/1/stock")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.PRODUCT_UPDATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_UPDATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.stock").isEqualTo(200);
    }

    // ==================== updateNameProduct ====================

    @Test
    @DisplayName("Should return 200 when name is updated successfully")
    void updateNameProduct_WhenValid_ShouldReturn200() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("New Name", 100, 1L);
        Product updatedProduct = Product.builder().id(1L).name("New Name").stock(100).branchId(1L).build();
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "New Name", 100, 1L);

        when(updateProductServicePort.updateNameProduct(1L, "New Name"))
                .thenReturn(Mono.just(updatedProduct));
        when(mapper.toResponseDto(updatedProduct)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/v1/products/1/name")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.PRODUCT_UPDATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.PRODUCT_UPDATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.name").isEqualTo("New Name");
    }

    // ==================== getTopStockProducts ====================

    @Test
    @DisplayName("Should return 200 with top stock products for a franchise")
    @SuppressWarnings("unchecked")
    void getTopStockProducts_WhenProductsExist_ShouldReturn200() {
        // Arrange
        TopStockProduct topProduct = TopStockProduct.builder()
                .branchId(1L).branchName("Branch A")
                .productId(1L).productName("Product A").stock(500)
                .build();
        TopStockProductResponseDTO responseDTO = new TopStockProductResponseDTO(
                1L, "Branch A", 1L, "Product A", 500);

        when(topProductServicePort.getTopStockProducts(1L))
                .thenReturn(Flux.just(topProduct));
        when(mapper.toResponseDto(any(List.class)))
                .thenReturn(List.of(responseDTO));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/products/1/top-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.TOP_STOCK_PRODUCTS.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.TOP_STOCK_PRODUCTS.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data[0].productName").isEqualTo("Product A")
                .jsonPath("$.data[0].stock").isEqualTo(500);
    }

    @Test
    @DisplayName("Should return 200 with empty list when no top stock products")
    @SuppressWarnings("unchecked")
    void getTopStockProducts_WhenNoProducts_ShouldReturn200WithEmptyList() {
        // Arrange
        when(topProductServicePort.getTopStockProducts(1L))
                .thenReturn(Flux.empty());
        when(mapper.toResponseDto(any(List.class)))
                .thenReturn(List.of());

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/products/1/top-stock")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(0);
    }
}
