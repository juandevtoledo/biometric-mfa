package com.lulobank.biometric.api.validation.otp;

import com.lulobank.biometric.api.validation.MFAJwtValidator;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;

public interface OTPJwtValidator extends MFAJwtValidator<OTPTokenValidationRequest, OTPTokenValidationResponse> {
    static String header() {
        return "otp-token";
    }
}
