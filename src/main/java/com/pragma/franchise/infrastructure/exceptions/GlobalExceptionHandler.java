package com.pragma.franchise.infrastructure.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.franchise.domain.exceptions.BadRequestException;
import com.pragma.franchise.domain.exceptions.BusinessException;
import com.pragma.franchise.domain.exceptions.NotFoundException;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
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
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import static com.pragma.franchise.infrastructure.constants.Constants.CONCURRENCY_ERROR;
import static com.pragma.franchise.infrastructure.constants.Constants.ERROR_MESSAGE;
import static com.pragma.franchise.infrastructure.constants.Constants.INTERNAL_ERROR;
import static com.pragma.franchise.infrastructure.constants.Constants.REQUEST_TIMEOUT;
import static com.pragma.franchise.infrastructure.constants.Constants.SERVICE_UNAVAILABLE;
import static com.pragma.franchise.infrastructure.constants.Constants.UNEXPECTED_ERROR;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@RequiredArgsConstructor
public final class GlobalExceptionHandler implements ErrorWebExceptionHandler {

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

    private final Map<Class<? extends Throwable>, Function<Throwable, GenericResponse<Object>>> EXCEPTION_HANDLERS = Map.of(
            CallNotPermittedException.class, ex -> buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE, ERROR_MESSAGE),
            BulkheadFullException.class, ex -> buildErrorResponse(HttpStatus.TOO_MANY_REQUESTS, CONCURRENCY_ERROR, ERROR_MESSAGE),
            TimeoutException.class, ex -> buildErrorResponse(HttpStatus.GATEWAY_TIMEOUT, REQUEST_TIMEOUT, ERROR_MESSAGE),
            BusinessException.class, ex -> buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), ERROR_MESSAGE),
            BadRequestException.class, ex -> buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ERROR_MESSAGE),
            NotFoundException.class, ex -> buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), ERROR_MESSAGE)
    );

    private GenericResponse<Object> mapExceptionToErrorResponse(final Throwable ex) {
        return EXCEPTION_HANDLERS.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(ex))
                .findFirst()
                .map(entry -> entry.getValue().apply(ex))
                .orElse(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR, ERROR_MESSAGE));
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
