package com.pragma.franchise.infrastructure.entrypoints.branch.handler;

import com.pragma.franchise.domain.api.branch.CreateBranchServicePort;
import com.pragma.franchise.domain.api.branch.UpdateBranchServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
import com.pragma.franchise.infrastructure.utils.RequestParamExtractor;
import com.pragma.franchise.infrastructure.utils.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.pragma.franchise.infrastructure.constants.Constants.BRANCH_ID;
import static com.pragma.franchise.infrastructure.constants.Constants.ID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BranchHandler {

    private final CreateBranchServicePort createBranchServicePort;
    private final UpdateBranchServicePort updateBranchServicePort;
    private final BranchMapper mapper;
    private final ValidatorHelper validator;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return request.bodyToMono(BranchRequestDTO.class)
                .flatMap(validator::validate)
                .map(mapper::toModel)
                .flatMap(createBranchServicePort::createBranch)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.BRANCH_CREATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.BRANCH_CREATED.getCode())
                                .data(mapper.toResponseDto(response)).build())
                );
    }

    public Mono<ServerResponse> updateNameBranch(ServerRequest request) {
        return Mono.zip(
                        RequestParamExtractor.extractLongPathVariable(request.pathVariable(ID), BRANCH_ID),
                        request.bodyToMono(ProductRequestDTO.class)
                )
                .flatMap(tuple -> updateBranchServicePort.updateNameBranch(tuple.getT1(), tuple.getT2().name()))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.BRANCH_UPDATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.BRANCH_UPDATED.getCode())
                                .data(mapper.toResponseDto(response))
                                .build())
                );
    }
}
