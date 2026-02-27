package com.pragma.franchise.infrastructure.entrypoints.branch;

import com.pragma.franchise.infrastructure.entrypoints.branch.dto.BranchRequestDTO;
import com.pragma.franchise.infrastructure.entrypoints.branch.handler.BranchHandler;
import com.pragma.franchise.infrastructure.entrypoints.dto.GenericResponse;
import com.pragma.franchise.infrastructure.entrypoints.product.dto.ProductRequestDTO;
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
public class BranchRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/branch",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Create a new branch",
                            tags = {"Branch"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/branch/{id}",
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateNameBranch",
                    operation = @Operation(
                            operationId = "updateNameBranch",
                            summary = "Update branch name",
                            tags = {"Branch"},
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                                            schema = @Schema(type = "integer", format = "int64"))
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated successfully",
                                            content = @Content(schema = @Schema(implementation = GenericResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> branchRouterFunction(BranchHandler handler) {
        return RouterFunctions.route()
                .POST("/api/v1/branch", handler::createBranch)
                .PATCH("/api/v1/branch/{id}", handler::updateNameBranch)
                .build();
    }
}
