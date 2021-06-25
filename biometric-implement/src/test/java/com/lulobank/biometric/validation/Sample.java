package com.lulobank.biometric.validation;

import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import com.lulobank.biometric.api.validation.biometric.dto.Content;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;

import static com.lulobank.biometric.validation.MockConstant.ID_CLIENT;
import static com.lulobank.biometric.validation.MockConstant.TRANSACTION_TYPE;

public class Sample {
    public static final BiometricTokenValidationRequest biometricTokenValidationRequest() {
        BiometricTokenValidationRequest request = new BiometricTokenValidationRequest();
        String biometricToken = "token";
        request.setBiometricToken(biometricToken);
        request.setClientId(ID_CLIENT);
        return request;
    }

    public static final BiometricTokenValidationResponse biometricTokenValidationResponse(String status) {
        BiometricTokenValidationResponse responseValidation = new BiometricTokenValidationResponse();
        Content content = new Content();
        content.setStatus(status);
        responseValidation.setContent(content);
        return responseValidation;
    }

    public static final OTPTokenValidationRequest otpTokenValidationRequest() {
        OTPTokenValidationRequest request = new OTPTokenValidationRequest();
        request.setClientId(ID_CLIENT);
        request.setTransactionType(TRANSACTION_TYPE);
        return request;
    }

    public static final OTPTokenValidationResponse otpTokenValidationResponse(boolean valid) {
        OTPTokenValidationResponse responseValidation = new OTPTokenValidationResponse(valid);
        return responseValidation;
    }
}
