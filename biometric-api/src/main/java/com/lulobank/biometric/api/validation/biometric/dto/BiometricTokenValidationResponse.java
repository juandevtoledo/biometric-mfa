package com.lulobank.biometric.api.validation.biometric.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BiometricTokenValidationResponse {
    private Content content;
}
