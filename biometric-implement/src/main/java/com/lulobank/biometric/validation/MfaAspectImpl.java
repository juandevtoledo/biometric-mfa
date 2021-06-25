package com.lulobank.biometric.validation;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.annotation.MFAType;
import com.lulobank.biometric.api.exception.MFAUnauthorizedException;
import com.lulobank.biometric.api.validation.MfaAspect;
import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.validation.biometric.BiometricJwtHandler;
import com.lulobank.biometric.validation.otp.OTPJwtHandler;
import io.vavr.API;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@Aspect
@RequiredArgsConstructor
public class MfaAspectImpl implements MfaAspect<JoinPoint> {
    private final HttpServletRequest servletRequest;
    private final BiometricJwtHandler biometricJwtHandler;
    private final OTPJwtHandler otpJwtHandler;
    private final ArgumentProcessor argumentProcessor;

    @Pointcut("@annotation(com.lulobank.biometric.api.annotation.MFA)")
    public void anyAnnotatedMethod() {
    }

    @Before("anyAnnotatedMethod()")
    public void validate(JoinPoint joinPoint) {
        Try.of(() -> getAnnotationMethod(joinPoint))
                .peek(this::processRequest);
    }

    @Around("anyAnnotatedMethod()")
    public Object execute(ProceedingJoinPoint joinPoint) {
        return Try.of(() -> getAnnotationMethod(joinPoint))
                .map(annotation -> processCall(joinPoint, annotation, servletRequest))
                .get();
    }

    private Object processCall(ProceedingJoinPoint joinPoint, MFA annotation, HttpServletRequest servletRequest) {
        return (MFAType.OTP == annotation.type() || MFAType.BIOMETRIC_PLUS_OTP == annotation.type()) ?
                proceedWithArgs(joinPoint, annotation.requestBodyClass(), servletRequest) :
                proceed(joinPoint);
    }

    private Object proceedWithArgs(ProceedingJoinPoint joinPoint, Class<?> type, HttpServletRequest servletRequest) {
        return Try.of(() -> argumentProcessor.getMethodArgs(servletRequest, type, joinPoint))
                .map(newArgs -> proceed(newArgs, joinPoint))
                .onFailure(e -> log.error(String.format("Error proceeding to the annotated method: %s", e.getMessage())))
                .get();
    }

    private Object proceed(Object[] args, ProceedingJoinPoint joinPoint) {
        return Try.of(() -> joinPoint.proceed(args))
                .onFailure(e -> log.error(String.format("Error proceeding to the annotated method: %s", e.getMessage())))
                .get();
    }

    private Object proceed(ProceedingJoinPoint joinPoint) {
        return Try.of(joinPoint::proceed)
                .onFailure(e -> log.error(String.format("Error proceeding to the annotated method: %s", e.getMessage())))
                .get();
    }

    private MFA getAnnotationMethod(JoinPoint joinPoint) {
        MethodSignature methodSign = (MethodSignature) joinPoint.getSignature();
        try {
            return methodSign.getMethod().getAnnotation(MFA.class);
        } catch (NullPointerException e) {
            throw new MFAUnauthorizedException("UNAUTHORIZED_MFA");
        }
    }

    private void processRequest(MFA annotation) {
        API.Match(annotation.type()).of(
                Case($(MFAType.BIOMETRIC_PLUS_OTP), () -> validateBiometricPlusOTP(annotation)),
                Case($(), () -> defaultValidation(annotation))
        );
    }

    private boolean validateBiometricPlusOTP(MFA annotation){
        if (biometricHeaderIsPresent() && otpHeaderIsPresent()) {
            biometricJwtHandler.validate(servletRequest, annotation);
            otpJwtHandler.validate(servletRequest, annotation);
        } else {
            throw new MFAUnauthorizedException("NO_MFA_TOKEN");
        }
        return true;
    }

    private boolean defaultValidation(MFA annotation){
        if (biometricHeaderIsPresent()) {
            biometricJwtHandler.validate(servletRequest, annotation);
        } else if (otpHeaderIsPresent()) {
            otpJwtHandler.validate(servletRequest, annotation);
        } else {
            throw new MFAUnauthorizedException("NO_MFA_TOKEN");
        }
        return true;
    }

    private Boolean biometricHeaderIsPresent(){
        return nonNull(servletRequest.getHeader(BiometricJwtValidator.header()));
    }

    private Boolean otpHeaderIsPresent(){
        return nonNull(servletRequest.getHeader(OTPJwtValidator.header()));
    }
}
