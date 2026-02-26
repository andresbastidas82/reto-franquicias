package com.pragma.franchise.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TechnicalMessage {

    INTERNAL_ERROR(500,"Something went wrong, please try again", ""),
    INVALID_REQUEST(400, "Bad Request, please verify data", ""),
    INVALID_PARAMETERS(400, "Bad Parameters, please verify data", ""),

    FRANCHISE_CREATED(201, "Franchise created successfully", ""),
    FRANCHISE_NOT_FOUND(404, "Franchise not found", ""),

    BRANCH_CREATED(201, "Branch created successfully", ""),
    BRANCH_NOT_FOUND(404, "Branch not found", ""),

    PRODUCT_CREATED(201, "Product created successfully", ""),
    PRODUCT_ALREADY_EXISTS(409, "Product already exists." , ""),
    PRODUCT_NOT_FOUND(404, "Product not found", ""),
    PRODUCT_DELETED(204, "Product deleted successfully", ""),
    PRODUCT_NOT_BRANCH(404, "Product not found in branch", ""),
    PRODUCT_UPDATED(200, "Product updated successfully", ""),
    TOP_STOCK_PRODUCTS(200, "Top stock products by branches", ""),

    UNSUPPORTED_OPERATION(501, "Method not supported, please try again", ""),
    FRANCHISE_ALREADY_EXISTS(409,"Franchise already exists." ,"" );

    private final int code;
    private final String message;
    private final String param;
}
