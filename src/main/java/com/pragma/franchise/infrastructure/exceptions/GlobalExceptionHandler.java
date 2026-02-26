package com.pragma.franchise.infrastructure.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public final class GlobalExceptionHandler implements ErrorWebExceptionHandler {
    private static final String UNEXPECTED_ERROR = "Unexpected error";
    private static final String ERROR_MESSAGE = "Error processing request";
    private static final String INTERNAL_ERROR = "An unexpected internal error occurred";
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        GenericResponse<Object> response = mapExceptionToErrorResponse(ex);

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(response.getStatusCode()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBufferFactory bufferFactory = exchange.getResponse().bufferFactory();

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(response);
            return exchange.getResponse()
                    .writeWith(Mono.just(bufferFactory.wrap(bytes)));
        } catch (Exception e) {
            log.error("Error writing response", e);
            return exchange.getResponse().setComplete();
        }
    }

    private GenericResponse<Object> mapExceptionToErrorResponse(final Throwable ex) {
        if (ex instanceof BusinessException businessEx) {
            return buildErrorResponse(HttpStatus.CONFLICT, businessEx.getMessage(), ERROR_MESSAGE);
        }
        if (ex instanceof BadRequestException badRequestEx) {
            return buildErrorResponse(HttpStatus.BAD_REQUEST, badRequestEx.getMessage(), ERROR_MESSAGE);
        }
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR, ERROR_MESSAGE);
    }

    private GenericResponse<Object> buildErrorResponse(HttpStatus status, String errorMessage, String message) {
        List<String> errors = errorMessage != null
                ? Arrays.asList(errorMessage.split("\\|"))
                : List.of(UNEXPECTED_ERROR);

        return GenericResponse.builder()
                .statusCode(status.value())
                .message(message)
                .isSuccess(false)
                .errors(errors)
                .build();
    }
}
