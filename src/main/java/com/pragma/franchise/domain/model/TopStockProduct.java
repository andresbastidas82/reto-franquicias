package com.pragma.franchise.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopStockProduct {
    private Long branchId;
    private String branchName;
    private Long productId;
    private String productName;
    private Integer stock;
}
