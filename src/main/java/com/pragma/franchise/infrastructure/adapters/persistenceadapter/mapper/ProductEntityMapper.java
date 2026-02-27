package com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface ProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toModel(ProductEntity productEntity);
}
