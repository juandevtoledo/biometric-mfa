package com.lulobank.biometric.validation.otp;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.biometric.validation.MFAJwtHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.biometric.util.Constant.HEADER_AUTHORIZATION;
import static com.lulobank.biometric.util.Constant.HEADER_CONTENT_TYPE;
import static com.lulobank.biometric.util.Constant.HEADER_CONTENT_TYPE_JSON;

@Slf4j
public class OTPJwtHandler extends MFAJwtHandler<OTPTokenValidationRequest, OTPTokenValidationResponse> {
    public OTPJwtHandler(OTPJwtValidator otpJwtValidator) {
        super(otpJwtValidator);
    }

    @Override
    protected Map<String, String> fillRequestHeaders(HttpServletRequest httpServletRequest) {
        Map<String, String> defaultHeaders = new HashMap<>();
        defaultHeaders.put(OTPJwtValidator.header(), httpServletRequest.getHeader(OTPJwtValidator.header()));
        defaultHeaders.put(HEADER_AUTHORIZATION, httpServletRequest.getHeader(HEADER_AUTHORIZATION));
        defaultHeaders.put(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_JSON);
        return defaultHeaders;
    }

    @Override
    protected OTPTokenValidationRequest buildValidationRequest(HttpServletRequest httpServletRequest, MFA annotation) {
        return new OTPTokenValidationRequest(retrieveClientFromRequest(httpServletRequest), annotation.transaction());
    }

    @Override
    protected void processResponse(OTPTokenValidationResponse tokenValidationResponse) {
        if (!tokenValidationResponse.isValid()) {
            throw new OTPUnauthorizedException("Invalid OTP token.");
        }
    }
}
