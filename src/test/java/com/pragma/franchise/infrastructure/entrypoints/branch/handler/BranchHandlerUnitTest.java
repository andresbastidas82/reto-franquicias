package com.pragma.franchise.infrastructure.entrypoints.branch.handler;

import com.pragma.franchise.domain.api.branch.CreateBranchServicePort;
import com.pragma.franchise.domain.api.branch.UpdateBranchServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.infrastructure.entrypoints.branch.BranchRouter;
import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchResponseDTO;
import com.pragma.franchise.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
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
class BranchHandlerUnitTest {

    @Mock
    private CreateBranchServicePort createBranchServicePort;
    @Mock
    private UpdateBranchServicePort updateBranchServicePort;
    @Mock
    private BranchMapper mapper;
    @Mock
    private ValidatorHelper validator;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        BranchHandler handler = new BranchHandler(
                createBranchServicePort, updateBranchServicePort,
                mapper, validator);
        BranchRouter router = new BranchRouter();
        webTestClient = WebTestClient
                .bindToRouterFunction(router.branchRouterFunction(handler))
                .build();
    }

    // ==================== createBranch ====================

    @Test
    @DisplayName("Should return 201 when branch is created successfully")
    void createBranch_WhenValid_ShouldReturn201() {
        // Arrange
        BranchRequestDTO requestDTO = new BranchRequestDTO("New Branch", 1L);
        Branch branch = Branch.builder().id(1L).name("New Branch").franchiseId(1L).build();
        BranchResponseDTO responseDTO = new BranchResponseDTO(1L, "New Branch", 1L);

        when(validator.validate(any(BranchRequestDTO.class)))
                .thenReturn(Mono.just(requestDTO));
        when(mapper.toModel(requestDTO)).thenReturn(branch);
        when(createBranchServicePort.createBranch(branch))
                .thenReturn(Mono.just(branch));
        when(mapper.toResponseDto(branch)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/branch")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.BRANCH_CREATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.BRANCH_CREATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("New Branch")
                .jsonPath("$.data.franchiseId").isEqualTo(1);
    }

    // ==================== updateNameBranch ====================

    @Test
    @DisplayName("Should return 200 when branch name is updated successfully")
    void updateNameBranch_WhenValid_ShouldReturn200() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("Updated Branch", 0, 1L);
        Branch updatedBranch = Branch.builder().id(1L).name("Updated Branch").franchiseId(1L).build();
        BranchResponseDTO responseDTO = new BranchResponseDTO(1L, "Updated Branch", 1L);

        when(updateBranchServicePort.updateNameBranch(1L, "Updated Branch"))
                .thenReturn(Mono.just(updatedBranch));
        when(mapper.toResponseDto(updatedBranch)).thenReturn(responseDTO);

        // Act & Assert
        webTestClient.patch()
                .uri("/api/v1/branch/1")
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.statusCode").isEqualTo(TechnicalMessage.BRANCH_UPDATED.getCode())
                .jsonPath("$.message").isEqualTo(TechnicalMessage.BRANCH_UPDATED.getMessage())
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.data.id").isEqualTo(1)
                .jsonPath("$.data.name").isEqualTo("Updated Branch")
                .jsonPath("$.data.franchiseId").isEqualTo(1);
    }
}
