package com.pragma.franchise.infrastructure.entrypoints.franchise.handler;

import com.pragma.franchise.domain.api.franchise.CreateFranchiseServicePort;
import com.pragma.franchise.domain.api.franchise.UpdateFranchiseServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.infrastructure.entrypoints.franchise.FranchiseRouter;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseResponseDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.mapper.FranchiseMapper;
import com.pragma.franchise.infrastructure.utils.ValidatorHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerUnitTest {

    @Mock
    private CreateFranchiseServicePort createFranchiseServicePort;
    @Mock
    private UpdateFranchiseServicePort updateFranchiseServicePort;
    @Mock
    private FranchiseMapper mapper;
    @Mock
    private ValidatorHelper validator;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        FranchiseHandler handler = new FranchiseHandler(
                createFranchiseServicePort, updateFranchiseServicePort,
                mapper, validator);
        FranchiseRouter router = new FranchiseRouter();
        webTestClient = WebTestClient
                .bindToRouterFunction(router.franchiseRouterFunction(handler))
                .build();
    }

    // ==================== createFranchise ====================

    @Test
    @DisplayName("Should return 201 when franchise is created successfully")
    void createFranchise_WhenValid_ShouldReturn201() {
        // Arrange
        FranchiseRequestDTO requestDTO = new FranchiseRequestDTO("New Franchise");
        Franchise franchise = Franchise.builder().id(1L).name("New Franchise").build();
        FranchiseResponseDTO responseDTO = new FranchiseResponseDTO(1L, "New Franchise");

        when(validator.validate(any(FranchiseRequestDTO.class)))
                .thenReturn(Mono.just(requestDTO));
        when(mapper.toModel(requestDTO)).thenReturn(franchise);
        when(createFranchiseServicePort.createFranchise(franchise))
                .thenReturn(Mono.just(franchise));
        when(mapper.toResponseDto(franchise)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/franchise")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.FRANCHISE_CREATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("New Franchise");
    }

    // ==================== updateNameFranchise ====================

    @Test
    @DisplayName("Should return 200 when franchise name is updated successfully")
    void updateNameFranchise_WhenValid_ShouldReturn200() {
        // Arrange
        FranchiseRequestDTO requestDTO = new FranchiseRequestDTO("Updated Franchise");
        Franchise updatedFranchise = Franchise.builder().id(1L).name("Updated Franchise").build();
        FranchiseResponseDTO responseDTO = new FranchiseResponseDTO(1L, "Updated Franchise");

        when(validator.validate(any(FranchiseRequestDTO.class)))
                .thenReturn(Mono.just(requestDTO));
        when(updateFranchiseServicePort.updateNameFranchise(1L, "Updated Franchise"))
                .thenReturn(Mono.just(updatedFranchise));
        when(mapper.toResponseDto(updatedFranchise)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/v1/franchise/1")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.FRANCHISE_UPDATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.FRANCHISE_UPDATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Updated Franchise");
    }
}
