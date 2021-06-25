package com.lulobank.biometric.api.validation.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OTPTokenValidationRequest {
    private String clientId;
    private String transactionType;
}
