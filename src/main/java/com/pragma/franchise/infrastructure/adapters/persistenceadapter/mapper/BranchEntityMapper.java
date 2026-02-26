package com.pragma.franchise.infrastructure.adapters.persistenceadapter.mapper;

import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.infrastructure.adapters.persistenceadapter.entity.BranchEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchEntityMapper {

    BranchEntity toEntity(Branch branch);

    Branch toModel(BranchEntity branchEntity);
}
