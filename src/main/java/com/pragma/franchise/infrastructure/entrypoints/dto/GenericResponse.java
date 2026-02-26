package com.pragma.franchise.infrastructure.entrypoints.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse <T> {
    private int statusCode;
    private String message;
    private T data;
    private boolean isSuccess;
    private List<String> errors;
}
