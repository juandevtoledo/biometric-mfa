package com.lulobank.biometric.api.validation.biometric.exception;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import lombok.Getter;

@Getter
public class BiometricUnauthorizedException extends MFAUnauthorizedException {
    public BiometricUnauthorizedException(String detail) {
        super(detail);
    }
}