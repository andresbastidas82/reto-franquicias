package com.pragma.franchise.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR(500,"Something went wrong, please try again", ""),
    INVALID_REQUEST(400, "Bad Request, please verify data", ""),
    FRANCHISE_CREATED(201, "Franchise created successfully", ""),
    INVALID_PARAMETERS(400, "Bad Parameters, please verify data", ""),
    UNSUPPORTED_OPERATION(501, "Method not supported, please try again", ""),
    FRANCHISE_ALREADY_EXISTS(409,"Franchise already exists." ,"" );

    private final int code;
    private final String message;
    private final String param;
}
