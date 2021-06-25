package com.lulobank.biometric.validation;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.annotation.MFAType;
import com.lulobank.biometric.api.annotation.SecurityType;
import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.biometric.validation.biometric.BiometricJwtHandler;
import com.lulobank.biometric.validation.otp.OTPJwtHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.lulobank.biometric.validation.MockConstant.ERROR_RESPONSE;
import static com.lulobank.biometric.validation.MockConstant.ID_CLIENT;
import static com.lulobank.biometric.validation.MockConstant.SUCCESSFUL_RESPONSE;
import static com.lulobank.biometric.validation.MockConstant.TOKEN;
import static com.lulobank.biometric.validation.Sample.biometricTokenValidationResponse;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MfaAspectImplTest {
    @Mock
    private JoinPoint joinPoint;
    @Mock
    private MethodSignature methodSignature;
    @Mock
    private Method method;
    @Mock
    private MFA mfaAnnotation;
    @Mock
    private BiometricJwtValidator biometricJwtValidator;
    @Mock
    private OTPJwtValidator otpJwtValidator;
    @Mock
    private HttpServletRequest servletRequest;
    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;
    @Mock
    private ArgumentProcessor argumentProcessor;
    private MfaAspectImpl mfaAspectImpl;

    private BiometricJwtHandler biometricJwtHandler;
    private OTPJwtHandler otpJwtHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        biometricJwtHandler = new BiometricJwtHandler(biometricJwtValidator);
        otpJwtHandler = new OTPJwtHandler(otpJwtValidator);
        mfaAspectImpl = new MfaAspectImpl(servletRequest, biometricJwtHandler, otpJwtHandler, argumentProcessor);
    }

    @Test(expected = BiometricUnauthorizedException.class)
    public void shouldReturnBiometricException() throws Exception {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("SET_PIN");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(BiometricJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(biometricJwtValidator.validateWithUserCredentials(anyMap(), any())).thenReturn(biometricTokenValidationResponse(ERROR_RESPONSE));
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = Test.None.class /* no exception expected */)
    public void shouldReturnBiometricValidationOK() throws Exception {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("SET_PIN");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(BiometricJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(biometricJwtValidator.validateWithUserCredentials(anyMap(), any())).thenReturn(biometricTokenValidationResponse(SUCCESSFUL_RESPONSE));
        mfaAspectImpl.validate(joinPoint);
    }

    @Test
    public void shouldReturnOTPValidationOK() throws Exception {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(otpJwtValidator.validateWithUserCredentials(any(), any())).thenReturn(new OTPTokenValidationResponse(true));
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldReturnForbiddenExceptionDueToInvalidOTPToken() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(otpJwtValidator.validateWithUserCredentials(any(), any())).thenReturn(new OTPTokenValidationResponse(false));
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = MFAUnauthorizedException.class)
    public void shouldReturnForbiddenExceptionDueToMissingToken() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = MFAUnauthorizedException.class)
    public void shouldReturnForbiddenExceptionDueToMissingTokensWhenMFATypeIsBIOMETRIC_PLUS_OTP() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.type()).thenReturn(MFAType.BIOMETRIC_PLUS_OTP);
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = MFAUnauthorizedException.class)
    public void shouldReturnForbiddenExceptionDueToMissingOTPTokenWhenMFATypeIsBIOMETRIC_PLUS_OTP() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.type()).thenReturn(MFAType.BIOMETRIC_PLUS_OTP);
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(BiometricJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        mfaAspectImpl.validate(joinPoint);
    }

    @Test(expected = MFAUnauthorizedException.class)
    public void shouldReturnForbiddenExceptionDueToMissingBiometricTokenWhenMFATypeIsBIOMETRIC_PLUS_OTP() {
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("LULO_TRANSFER");
        when(mfaAnnotation.type()).thenReturn(MFAType.BIOMETRIC_PLUS_OTP);
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        mfaAspectImpl.validate(joinPoint);
    }

    @Test
    public void shouldReturnBiometricAndOTPValidationOKWhenMFATypeIsBIOMETRIC_PLUS_OTP(){
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(mfaAnnotation.transaction()).thenReturn("SET_PIN");
        when(mfaAnnotation.securityType()).thenReturn(SecurityType.USER_CREDENTIALS);
        when(servletRequest.getHeader(BiometricJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn(TOKEN);
        when(servletRequest.getAttribute(any())).thenReturn(getPathVariables());
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(biometricJwtValidator.validateWithUserCredentials(anyMap(), any())).thenReturn(biometricTokenValidationResponse(SUCCESSFUL_RESPONSE));
        when(otpJwtValidator.validateWithUserCredentials(any(), any())).thenReturn(new OTPTokenValidationResponse(true));
        mfaAspectImpl.validate(joinPoint);
    }

    @Test
    public void shouldProceedOkWithNewArgsWhenMFATypeIsBIOMETRIC_PLUS_OTP() throws Throwable {
        Object[] newArgs = new Object[]{"TEST_ARGS_1", 1};
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(mfaAnnotation.type()).thenReturn(MFAType.BIOMETRIC_PLUS_OTP);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"ORIGINAL_TEST_1", 1});
        when(argumentProcessor.getMethodArgs(any(HttpServletRequest.class), any(), any()))
                .thenReturn(newArgs);
        when(proceedingJoinPoint.proceed(any())).thenReturn(new Object());

        Object execute = mfaAspectImpl.execute(proceedingJoinPoint);

        assertThat(execute, notNullValue());

        verify(proceedingJoinPoint).proceed(newArgs);
        verify(proceedingJoinPoint, times(0)).proceed();
    }

    @Test
    public void shouldProceedOkWithNewArgsWhenMFATypeIsOTP() throws Throwable {
        Object[] newArgs = new Object[]{"TEST_ARGS_1", 1};
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(mfaAnnotation.type()).thenReturn(MFAType.OTP);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"ORIGINAL_TEST_1", 1});
        when(argumentProcessor.getMethodArgs(any(HttpServletRequest.class), any(), any()))
                .thenReturn(newArgs);
        when(proceedingJoinPoint.proceed(any())).thenReturn(new Object());

        Object execute = mfaAspectImpl.execute(proceedingJoinPoint);

        assertThat(execute, notNullValue());

        verify(proceedingJoinPoint).proceed(newArgs);
        verify(proceedingJoinPoint, times(0)).proceed();
    }

    @Test
    public void shouldProceedOkWithOriginalArgsWhenMFATypeIsNotOTP() throws Throwable {
        Object[] args = new Object[]{"ORIGINAL_TEST_1", 1};
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(mfaAnnotation.type()).thenReturn(MFAType.BIOMETRIC);
        when(proceedingJoinPoint.proceed()).thenReturn(new Object());

        Object execute = mfaAspectImpl.execute(proceedingJoinPoint);

        assertThat(execute, notNullValue());

        verify(proceedingJoinPoint).proceed();
        verify(proceedingJoinPoint, times(0)).proceed(args);
    }

    @Test(expected = MFAUnauthorizedException.class)
    public void shouldProceedOkWhenFailure() throws Throwable {
        Object[] args = new Object[]{"ORIGINAL_TEST_1", 1};
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(null);
        when(mfaAnnotation.type()).thenReturn(MFAType.OTP);
        when(proceedingJoinPoint.proceed()).thenReturn(new Object());

        Object execute = mfaAspectImpl.execute(proceedingJoinPoint);

        assertThat(execute, notNullValue());

        verify(proceedingJoinPoint).proceed();
        verify(proceedingJoinPoint, times(0)).proceed(args);
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldFailWhenRequestBodyParsingError() throws Throwable {
        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getMethod()).thenReturn(method);
        when(method.getAnnotation(any())).thenReturn(mfaAnnotation);
        when(mfaAnnotation.type()).thenReturn(MFAType.OTP);
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"ORIGINAL_TEST_1", 1});
        when(argumentProcessor.getMethodArgs(any(HttpServletRequest.class), any(), any()))
                .thenThrow(new OTPUnauthorizedException("Error invalid header."));
        mfaAspectImpl.execute(proceedingJoinPoint);
    }

    private Map<String, String> getPathVariables() {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("idClient", ID_CLIENT);
        return pathVariables;
    }
}