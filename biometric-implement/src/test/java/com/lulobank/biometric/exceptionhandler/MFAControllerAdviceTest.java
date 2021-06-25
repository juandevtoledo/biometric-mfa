package com.lulobank.biometric.exceptionhandler;

import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.biometric.exceptionhandler.dto.ErrorHandler;
import com.lulobank.biometric.exceptionhandler.dto.ErrorMFA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MFAControllerAdviceTest {
    @InjectMocks
    private MFAControllerAdvice testedClass;

    @Test
    public void shouldReturnResponseEntity() {
        OTPUnauthorizedException exception = new OTPUnauthorizedException("detail");
        ResponseEntity<ErrorHandler> responseEntity = testedClass.handlerRunTimeException(exception);
        assertEquals(406, responseEntity.getStatusCode().value());
        assertEquals(Integer.valueOf("406"), responseEntity.getBody().getCode());
        assertEquals("Unauthorized token", responseEntity.getBody().getDetail());
        assertEquals(exception.getDetail(), responseEntity.getBody().getFailure());
    }
    @Test
    public void shouldGenericBiometricErrorOnResponseEntity() {
        BiometricUnauthorizedException exception = new BiometricUnauthorizedException("invalid biometric token");
        ResponseEntity<ErrorMFA> responseEntity = testedClass.biometricMFAExceptionHandler(exception);
        assertEquals(406, responseEntity.getStatusCode().value());
        assertEquals("GEN_043", responseEntity.getBody().getCode());
        assertEquals("V", responseEntity.getBody().getDetail());
        assertEquals(exception.getDetail(), responseEntity.getBody().getFailure());
    }
}
