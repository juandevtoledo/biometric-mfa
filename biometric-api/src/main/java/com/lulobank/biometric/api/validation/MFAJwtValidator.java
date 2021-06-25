package com.lulobank.biometric.api.validation;

import java.util.Map;

public interface MFAJwtValidator<R, T> {
    T validateWithClientCredentials(Map<String, String> headers, R tokenValidationRequest);

    T validateWithUserCredentials(Map<String, String> headers, R tokenValidationRequest);
}
