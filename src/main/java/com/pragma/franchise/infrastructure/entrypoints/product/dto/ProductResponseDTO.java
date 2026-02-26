package com.pragma.franchise.infrastructure.entrypoints.product.dto;

public record ProductResponseDTO(Long id, String name, Integer stock, Long branchId) {
}
