package com.pragma.franchise.infrastructure.entrypoints.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductRequestDTO(
        @NotNull @NotBlank String name,
        @NotNull Integer stock,
        @NotNull Long branchId
) {
}
