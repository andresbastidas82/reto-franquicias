package com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.pragma.franchise.domain.model.Product;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductEntityMapper {

    ProductEntity toEntity(Product product);

    Product toModel(ProductEntity productEntity);
}
