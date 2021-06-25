package com.lulobank.biometric.api.validation.biometric;

import com.lulobank.biometric.api.validation.MFAJwtValidator;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;

public interface BiometricJwtValidator extends MFAJwtValidator<BiometricTokenValidationRequest, BiometricTokenValidationResponse> {
    static String header() {
        return "biometric-token";
    }
}
