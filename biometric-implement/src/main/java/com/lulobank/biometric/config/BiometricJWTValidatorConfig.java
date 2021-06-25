package com.lulobank.biometric.config;

import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.validation.RetrofitFactory;
import com.lulobank.biometric.validation.biometric.BiometricJwtHandler;
import com.lulobank.biometric.validation.biometric.BiometricJwtOperations;
import com.lulobank.biometric.validation.biometric.BiometricJwtValidatorImpl;
import com.lulobank.biometric.validation.biometric.mock.BiometricJwtValidatorMock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BiometricJWTValidatorConfig {
    @Value("${services.authentication.url:http://localhost:8100/}")
    private String serviceDomain;

    @Bean
    public BiometricJwtOperations retrofitBiometricJWTOperations() {
        return RetrofitFactory.create(serviceDomain, BiometricJwtOperations.class);
    }

    @Bean
    @ConditionalOnProperty(name = "services.authentication.mock", havingValue = "true")
    public BiometricJwtValidator biometricJWTValidatorMock() {
        return new BiometricJwtValidatorMock();
    }

    @Bean
    @ConditionalOnProperty(name = "services.authentication.mock", havingValue = "false", matchIfMissing = true)
    @Primary
    public BiometricJwtValidator biometricJWTValidatorImpl(BiometricJwtOperations retrofitBiometricJWTOperations) {
        return new BiometricJwtValidatorImpl(retrofitBiometricJWTOperations);
    }

    @Bean
    public BiometricJwtHandler biometricJwtHandler(BiometricJwtValidator biometricJwtValidator) {
        return new BiometricJwtHandler(biometricJwtValidator);
    }
}
