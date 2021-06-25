package com.lulobank.biometric.api.validation.biometric.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BiometricTokenValidationRequest {
    private String clientId;
    private String biometricToken;
    private String transactionType;
}
