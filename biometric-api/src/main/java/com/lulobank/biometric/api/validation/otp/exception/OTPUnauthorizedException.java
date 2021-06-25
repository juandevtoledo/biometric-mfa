package com.lulobank.biometric.api.validation.otp.exception;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import lombok.Getter;

@Getter
public class OTPUnauthorizedException extends MFAUnauthorizedException {
    public OTPUnauthorizedException(String detail) {
        super(detail);
    }
}
