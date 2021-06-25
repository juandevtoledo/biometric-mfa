package com.lulobank.biometric.validation.biometric;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.validation.MFAJwtHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.biometric.util.Constant.HEADER_AUTHORIZATION;
import static com.lulobank.biometric.util.Constant.HEADER_CONTENT_TYPE;
import static com.lulobank.biometric.util.Constant.HEADER_CONTENT_TYPE_JSON;

@Slf4j
public class BiometricJwtHandler extends MFAJwtHandler<BiometricTokenValidationRequest, BiometricTokenValidationResponse> {
    private static final String BIOMETRIC_RESPONSE_OK = "14";

    public BiometricJwtHandler(BiometricJwtValidator biometricJwtValidator) {
        super(biometricJwtValidator);
    }

    @Override
    protected Map<String, String> fillRequestHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.put(HEADER_AUTHORIZATION, httpServletRequest.getHeader(HEADER_AUTHORIZATION));
        defaultHeaders.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_JSON);
        return defaultHeaders;
    }

    @Override
    protected BiometricTokenValidationRequest buildValidationRequest(HttpServletRequest httpServletRequest, MFA annotation) {
        String token = httpServletRequest.getHeader(BiometricJwtValidator.header());
        return new BiometricTokenValidationRequest(retrieveClientFromRequest(httpServletRequest), token,
                annotation.transaction());
    }

    @Override
    protected void processResponse(BiometricTokenValidationResponse tokenValidationResponse) {
        if (!BIOMETRIC_RESPONSE_OK.equals(tokenValidationResponse.getContent().getStatus())) {
            throw new BiometricUnauthorizedException("Invalid biometric token.");
        }
    }
}

