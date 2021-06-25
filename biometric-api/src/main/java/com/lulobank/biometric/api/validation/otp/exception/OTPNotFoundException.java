package com.lulobank.biometric.api.validation.otp.exception;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import lombok.Getter;

@Getter
public class OTPNotFoundException extends MFAUnauthorizedException {
    public OTPNotFoundException(String detail) {
        super(detail);
    }
}
