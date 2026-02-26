package com.pragma.franchise.infrastructure.utils;

import com.pragma.franchise.domain.exceptions.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ValidatorHelper {

    private final Validator validator;

    public <T> Mono<T> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            List<String> errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .toList();
            return Mono.error(new BadRequestException(String.join("|", errors)));
        }
        return Mono.just(object);
    }
}
