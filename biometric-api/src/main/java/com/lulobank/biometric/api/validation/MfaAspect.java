package com.lulobank.biometric.api.validation;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;

public interface MfaAspect<T> {
    void validate(T joinPoint) throws MFAUnauthorizedException;
}
