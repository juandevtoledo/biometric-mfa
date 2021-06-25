package com.lulobank.biometric.api.exception;

import lombok.Getter;

@Getter
public class MFAUnauthorizedException extends RuntimeException {
    private String detail;

    public MFAUnauthorizedException(String detail) {
        super("Unauthorized to access the resource");
        this.detail = detail;
    }
}
