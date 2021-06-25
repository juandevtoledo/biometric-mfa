package com.lulobank.biometric.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

@Slf4j
public class ArgumentProcessor {

    public Object[] getMethodArgs(HttpServletRequest servletRequest, Class<?> type, ProceedingJoinPoint joinPoint) {
        ObjectMapper mapper = new ObjectMapper().configure(FAIL_ON_UNKNOWN_PROPERTIES, false);
        return Try.of(() -> servletRequest.getHeader(OTPJwtValidator.header()))
                .map(this::splitHeader)
                .map(this::decodePayload)
                .mapTry(payloadDecoded -> mapper.readValue(payloadDecoded, type))
                .map(payload -> castBodyToType(payload, type, joinPoint))
                .onFailure(e -> log.error(String.format("Error parsing payload to defined class: %s, %s", type.toString(), e.getMessage())))
                .getOrElseThrow(() -> new OTPUnauthorizedException("UNAUTHORIZED_MFA_OTP"));
    }

    private byte[] decodePayload(Option<Tuple2<String, String>> otpHeader) {
        return Base64.getDecoder().decode(otpHeader.get()._2);
    }

    private Object[] castBodyToType(Object payload, Class<?> type, ProceedingJoinPoint joinPoint) {
        return Option.of(findParameterIndex(type, joinPoint))
                .filter(index -> index >= 0)
                .map(index -> Array.of(joinPoint.getArgs())
                        .zipWithIndex()
                        .map(tuple -> index.equals(tuple._2) ? payload : tuple._1)
                        .toJavaArray())
                .getOrElseThrow(() -> new OTPUnauthorizedException("UNAUTHORIZED_MFA_OTP"));
    }

    private Integer findParameterIndex(Class<?> type, ProceedingJoinPoint joinPoint) {
        MethodSignature methodSign = (MethodSignature) joinPoint.getSignature();
        return Array.of(methodSign.getParameterTypes()).indexOf(type);
    }

    private Option<Tuple2<String, String>> splitHeader(String header) {
        return Option.of(header)
                .map(value -> value.split(":"))
                .map(array -> Tuple.of(array[0], array[1]));
    }
}
