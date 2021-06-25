package com.lulobank.biometric.validation.biometric.mock;

import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import com.lulobank.biometric.api.validation.biometric.dto.Content;

import java.util.Map;

public class BiometricJwtValidatorMock implements BiometricJwtValidator {
    @Override
    public BiometricTokenValidationResponse validateWithClientCredentials(Map<String, String> headers,
            BiometricTokenValidationRequest tokenValidationRequest) {
        return new BiometricTokenValidationResponse(new Content("14"));
    }

    @Override
    public BiometricTokenValidationResponse validateWithUserCredentials(Map<String, String> headers,
            BiometricTokenValidationRequest tokenValidationRequest) {
        return new BiometricTokenValidationResponse(new Content("14"));
    }
}
