package com.pragma.franchise.infrastructure.entrypoints.product.handler;


import com.pragma.franchise.domain.api.product.CreateProductServicePort;
import com.pragma.franchise.domain.api.product.DeleteProductServicePort;
import com.pragma.franchise.domain.api.product.UpdateProductServicePort;
import com.pragma.franchise.domain.enums.TechnicalMessage;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.mapper.ProductMapper;
import com.pragma.franchise.infrastructure.utils.RequestParamExtractor;
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
public class ProductHandler {

    private final CreateProductServicePort createProductServicePort;
    private final DeleteProductServicePort deleteProductServicePort;
    private final UpdateProductServicePort updateProductServicePort;
    private final ProductMapper mapper;
    private final ValidatorHelper validator;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        return request.bodyToMono(ProductRequestDTO.class)
                .flatMap(validator::validate)
                .map(mapper::toModel)
                .flatMap(createProductServicePort::createProduct)
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.PRODUCT_CREATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.PRODUCT_CREATED.getCode())
                                .data(response).build())
                );
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        return RequestParamExtractor
                .extractLongPathVariable(request.pathVariable("id"), "Product id")
                .flatMap(deleteProductServicePort::deleteProductById)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> updateStockProduct(ServerRequest request) {
        return Mono.zip(
                        RequestParamExtractor.extractLongPathVariable(request.pathVariable("id"), "Product id"),
                        request.bodyToMono(ProductRequestDTO.class)
                )
                .flatMap(tuple -> updateProductServicePort.updateStockProduct(tuple.getT1(), tuple.getT2().stock()))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.PRODUCT_UPDATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.PRODUCT_UPDATED.getCode())
                                .data(response)
                                .build())
                );
    }

    public Mono<ServerResponse> updateNameProduct(ServerRequest request) {
        return Mono.zip(
                        RequestParamExtractor.extractLongPathVariable(request.pathVariable("id"), "Product id"),
                        request.bodyToMono(ProductRequestDTO.class)
                )
                .flatMap(tuple -> updateProductServicePort.updateNameProduct(tuple.getT1(), tuple.getT2().name()))
                .flatMap(response -> ServerResponse
                        .status(HttpStatus.OK)
                        .bodyValue(GenericResponse.builder()
                                .message(TechnicalMessage.PRODUCT_UPDATED.getMessage())
                                .isSuccess(true)
                                .statusCode(TechnicalMessage.PRODUCT_UPDATED.getCode())
                                .data(response)
                                .build())
                );
    }

}
