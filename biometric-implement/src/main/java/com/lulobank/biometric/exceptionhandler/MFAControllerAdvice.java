package com.lulobank.biometric.exceptionhandler;

import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.exceptionhandler.dto.ErrorHandler;
import com.lulobank.biometric.exceptionhandler.dto.ErrorMFA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@Slf4j
@ControllerAdvice
public class MFAControllerAdvice {
    private static final String FAILURE_MESSAGE = "Unauthorized token";

    @ExceptionHandler(MFAUnauthorizedException.class)
    public ResponseEntity<ErrorHandler> handlerRunTimeException(MFAUnauthorizedException ex) {
        ErrorHandler error = getErrorHandler(ex.getDetail());
        return ResponseEntity
            .status(error.getCode())
            .body(error);
    }
    @ExceptionHandler(BiometricUnauthorizedException.class)
    public ResponseEntity<ErrorMFA> biometricMFAExceptionHandler(BiometricUnauthorizedException ex) {
        ErrorMFA error = ErrorMFA.unAuthorizedBiometricToken(ex.getDetail());
        return ResponseEntity
            .status(NOT_ACCEPTABLE)
            .body(error);
    }

    private ErrorHandler getErrorHandler(String detail) {
        return ErrorHandler.builder()
            .failure(detail)
            .code(NOT_ACCEPTABLE.value())
            .detail(FAILURE_MESSAGE)
            .build();
    }

}
