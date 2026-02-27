package com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.FranchiseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FranchiseEntityMapper {

    FranchiseEntity toEntity(Franchise franchise);

    Franchise toModel(FranchiseEntity franchiseEntity);
}
