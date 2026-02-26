package com.pragma.franchise.infrastructure.utils;

import com.pragma.franchise.domain.exceptions.BadRequestException;
import reactor.core.publisher.Mono;

public final class RequestParamExtractor {

    private RequestParamExtractor() {
    }

    public static Mono<Long> extractLongPathVariable(String value, String fieldName) {

        return Mono.justOrEmpty(value)
                .switchIfEmpty(Mono.error(new BadRequestException(fieldName + " is required")))
                .flatMap(val -> {
                    try {
                        return Mono.just(Long.parseLong(val));
                    } catch (NumberFormatException e) {
                        return Mono.error(new BadRequestException(fieldName + " must be a number"));
                    }
                });
    }
}
