package com.lulobank.biometric.config;

import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.validation.RetrofitFactory;
import com.lulobank.biometric.validation.otp.OTPJwtHandler;
import com.lulobank.biometric.validation.otp.OtpJwtOperations;
import com.lulobank.biometric.validation.otp.OtpJwtValidatorImpl;
import com.lulobank.biometric.validation.otp.mock.OTPJwtValidatorMock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OTPJwtValidatorConfig {
    @Value("${services.otp.url:http://localhost:8083/}")
    private String serviceDomain;

    @Bean
    public OtpJwtOperations retrofitOtpJwtOperations() {
        return RetrofitFactory.create(serviceDomain, OtpJwtOperations.class);
    }

    @Bean
    @ConditionalOnProperty(name = "services.otp.mock", havingValue = "true")
    public OTPJwtValidator otpJwtValidatorMock() {
        return new OTPJwtValidatorMock();
    }

    @Bean
    @ConditionalOnProperty(name = "services.otp.mock", havingValue = "false", matchIfMissing = true)
    public OTPJwtValidator otpJWTValidatorImpl(OtpJwtOperations otpJwtOperations) {
        return new OtpJwtValidatorImpl(otpJwtOperations);
    }

    @Bean
    public OTPJwtHandler otpJwtHandler(OTPJwtValidator otpJwtValidator) {
        return new OTPJwtHandler(otpJwtValidator);
    }
}
