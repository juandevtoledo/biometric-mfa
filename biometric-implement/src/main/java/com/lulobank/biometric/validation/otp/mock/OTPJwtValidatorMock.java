package com.lulobank.biometric.validation.otp.mock;

import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class OTPJwtValidatorMock implements OTPJwtValidator {
    @Override
    public OTPTokenValidationResponse validateWithClientCredentials(Map<String, String> headers,
            OTPTokenValidationRequest tokenValidationRequest) {
        log.info("OTPJwtValidator.validateWithClientCredentials mock OK.");
        return new OTPTokenValidationResponse(true);
    }

    @Override
    public OTPTokenValidationResponse validateWithUserCredentials(Map<String, String> headers,
            OTPTokenValidationRequest tokenValidationRequest) {
        log.info("OTPJwtValidator.validateWithUserCredentials mock OK.");
        return new OTPTokenValidationResponse(true);
    }
}
