package com.pragma.franchise.infrastructure.entrypoints.product.mapper;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.domain.model.TopStockProduct;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductResponseDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.TopStockProductResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    Product toModel(ProductRequestDTO productRequestDTO);

    ProductResponseDTO toResponseDto(Product product);

    List<TopStockProductResponseDTO> toResponseDto(List<TopStockProduct> products);
}
