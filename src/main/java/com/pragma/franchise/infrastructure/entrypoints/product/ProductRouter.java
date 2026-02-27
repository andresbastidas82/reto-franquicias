package com.pragma.franchise.infrastructure.entrypoints.product;

import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.product.handler.ProductHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create a new product",
                            tags = {"Product"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/{id}/stock",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStockProduct",
                    operation = @Operation(
                            operationId = "updateStockProduct",
                            summary = "Update product stock",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product stock updated",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/{id}/name",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateNameProduct",
                    operation = @Operation(
                            operationId = "updateNameProduct",
                            summary = "Update product name",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/products/{franchiseId}/top-stock",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getTopStockProducts",
                    operation = @Operation(
                            operationId = "getTopStockProducts",
                            summary = "Get top stock products by franchise",
                            tags = {"Product"},
                            parameters = {
                                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Top stock products retrieved",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productsRouter(ProductHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/products", handler::createProduct)
                .DELETE("/api/v1/products/{id}", handler::deleteProduct)
                .PATCH("/api/v1/products/{id}/stock", handler::updateStockProduct)
                .PATCH("/api/v1/products/{id}/name", handler::updateNameProduct)
                .GET("/api/v1/products/{franchiseId}/top-stock", handler::getTopStockProducts)
                .build();
    }
}
