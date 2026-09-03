package com.payflow.common.exception;

import lombok.Getter;

@Getter
public class PayflowException extends RuntimeException {

    private final String errorCode;

    public PayflowException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PayflowException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public PayflowException(String message){
        super(message);
        this.errorCode = "PAYFLOW_ERROR";
    }
}
