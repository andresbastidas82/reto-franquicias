package com.pragma.franchise.infrastructure.entrypoints.franchise.mapper;

import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface FranchiseMapper {

    Franchise toModel(FranchiseRequestDTO franchiseRequestDTO);

    FranchiseResponseDTO toResponseDto(Franchise franchise);
}
