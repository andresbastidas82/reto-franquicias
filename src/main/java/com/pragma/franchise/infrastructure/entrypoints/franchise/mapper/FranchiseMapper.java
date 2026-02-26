package com.pragma.franchise.infrastructure.entrypoints.franchise.mapper;

import com.pragma.franchise.domain.model.Franchise;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    Franchise toModel(FranchiseRequestDTO franchiseRequestDTO);

    FranchiseResponseDTO toResponseDto(Franchise franchise);
}
