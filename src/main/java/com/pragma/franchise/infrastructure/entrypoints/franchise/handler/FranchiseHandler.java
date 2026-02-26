package com.pragma.franchise.infrastructure.entrypoints.franchise.handler;

import com.pragma.franchise.domain.api.franchise.CreateFranchiseServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.mapper.FranchiseMapper;
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
public class FranchiseHandler {

    private final CreateFranchiseServicePort createFranchiseServicePort;
    private final FranchiseMapper mapper;
    private final ValidatorHelper validator;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(FranchiseRequestDTO.class)
                .flatMap(validator::validate)
                .map(mapper::toModel)
                .flatMap(createFranchiseServicePort::createFranchise)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.FRANCHISE_CREATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.FRANCHISE_CREATED.getCode())
                                .data(response).build())
                );
    }
}
