package com.pragma.franchise.infrastructure.entrypoints.product.dto;

public record TopStockProductResponseDTO(
     Long branchId,
     String branchName,
     Long productId,
     String productName,
     Integer stock)
{ }
