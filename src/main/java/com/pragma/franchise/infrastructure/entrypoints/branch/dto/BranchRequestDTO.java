package com.pragma.franchise.infrastructure.entrypoints.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BranchRequestDTO(
        @NotNull @NotBlank String name,
        @NotNull Long franchiseId
) {
}
