package com.pragma.franchise.infrastructure.entrypoints.branch.mapper;

import com.pragma.franchise.domain.model.Branch;
import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BranchMapper {

    Branch toModel(BranchRequestDTO branchRequestDTO);
}
