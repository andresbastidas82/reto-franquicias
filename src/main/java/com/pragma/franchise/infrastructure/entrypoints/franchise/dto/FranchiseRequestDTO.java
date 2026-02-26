package com.pragma.franchise.infrastructure.entrypoints.franchise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FranchiseRequestDTO(@NotNull @NotBlank String name) {
}
