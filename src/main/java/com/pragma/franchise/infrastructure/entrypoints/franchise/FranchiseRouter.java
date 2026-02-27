package com.pragma.franchise.infrastructure.entrypoints.franchise;

import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.franchise.dto.FranchiseRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.franchise.handler.FranchiseHandler;
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
public class FranchiseRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/franchise",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchise"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/franchise/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateNameFranchise",
                    operation = @Operation(
                            operationId = "updateNameFranchise",
                            summary = "Update franchise name",
                            tags = {"Franchise"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> franchiseRouterFunction(FranchiseHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/franchise", handler::createFranchise)
                .PATCH("/api/v1/franchise/{id}", handler::updateNameFranchise)
                .build();
    }
}
