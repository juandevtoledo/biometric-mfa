package com.lulobank.biometric.exceptionhandler.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @apiNote This enum must handle business codes from GEN_160 onwards
 */
@Getter
@RequiredArgsConstructor
public enum GeneralErrorStatus {
    GEN_043("Unauthorized MFA token"),
    ;

    private final String message;

    public static final String DEFAULT_DETAIL = "V";
}
