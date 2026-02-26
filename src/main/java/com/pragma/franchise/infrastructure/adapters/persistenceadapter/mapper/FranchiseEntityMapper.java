package com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseEntityMapper {

    FranchiseEntity toEntity(Franchise franchise);

    Franchise toModel(FranchiseEntity franchiseEntity);
}
