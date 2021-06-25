package com.lulobank.biometric.api.validation.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OTPTokenValidationResponse {
    private boolean valid;
}
