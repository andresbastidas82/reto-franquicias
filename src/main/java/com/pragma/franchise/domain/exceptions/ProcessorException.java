package com.pragma.franchise.domain.exceptions;

import com.pragma.franchise.domain.enums.TechnicalMessage;
import lombok.Getter;

@Getter
public class ProcessorException extends RuntimeException {

    private final TechnicalMessage technicalMessage;

    public ProcessorException(Throwable cause, TechnicalMessage message) {
        super(message.getMessage(), cause);
        technicalMessage = message;
    }

    public ProcessorException(String message, TechnicalMessage technicalMessage) {
        super(message);
        this.technicalMessage = technicalMessage;
    }
}
