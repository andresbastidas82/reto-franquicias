package com.pragma.franchise.infrastructure.entrypoints.branch.handler;

import com.pragma.franchise.domain.api.BranchServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.branch.mapper.BranchMapper;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.utils.ValidatorHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BranchHandler {

    private final BranchServicePort branchServicePort;
    private final BranchMapper mapper;
    private final ValidatorHelper validator;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        return request.bodyToMono(BranchRequestDTO.class)
                .flatMap(validator::validate)
                .map(mapper::toModel)
                .flatMap(branchServicePort::createBranch)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.BRANCH_CREATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.BRANCH_CREATED.getCode())
                                .data(response).build())
                );
    }
}
