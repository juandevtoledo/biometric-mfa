package com.lulobank.biometric.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MFA {
    String transaction();

    MFAType type() default MFAType.DYNAMIC;

    SecurityType securityType() default SecurityType.USER_CREDENTIALS;

    Class<?> requestBodyClass();
}
