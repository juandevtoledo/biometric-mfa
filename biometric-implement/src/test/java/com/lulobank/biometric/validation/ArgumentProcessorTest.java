package com.lulobank.biometric.validation;

import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static java.lang.Boolean.TRUE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

public class ArgumentProcessorTest {

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private MethodSignature methodSignature;

    private ArgumentProcessor argumentProcessor;

    private final Object[] originalArguments = new Object[]{"TEST_ARGUMENT_1", 2, true};

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        argumentProcessor = new ArgumentProcessor();

        when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
    }

    @Test
    public void shouldGetMethodArgsWithNewArguments() {
        when(proceedingJoinPoint.getArgs()).thenReturn(originalArguments);
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class, Boolean.class});
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:Ik5FV19TVFJJTkdfQVJHVU1FTlQi");

        Object[] methodArgs = argumentProcessor.getMethodArgs(servletRequest, String.class, proceedingJoinPoint);

        assertThat(methodArgs, notNullValue());
        assertThat(methodArgs[0], is("NEW_STRING_ARGUMENT"));
        assertThat(methodArgs[1], is(2));
        assertThat(methodArgs[2], is(TRUE));
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldFailWhenHeaderStructureInvalid() {
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("Ik5FV19TVFJJTkdfQVJHVU1FTlQi");

        argumentProcessor.getMethodArgs(servletRequest, String.class, proceedingJoinPoint);
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldFailWhenHeaderIsNotEncoded() {
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:NOT_A_BASE64_HASH");

        argumentProcessor.getMethodArgs(servletRequest, String.class, proceedingJoinPoint);
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldFailWhenClassTypeDoesNotMatchWithHeader() {
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class, Boolean.class});
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:eyJrZXkiOiJ2YWx1ZSJ9");

        argumentProcessor.getMethodArgs(servletRequest, TestClass.class, proceedingJoinPoint);
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void shouldFailWhenClassTypeDoesNotMatchWithAnyOfOriginalArgs() {
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class, Boolean.class});
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:MTAwMDAuMDAx");

        argumentProcessor.getMethodArgs(servletRequest, Double.class, proceedingJoinPoint);
    }

    @Test
    public void shouldGetMethodArgsWithNewArgumentsWhenObjectHeader() {
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"TEST_ARGUMENT_1", 2, new TestClass("JSON_ARGUMENT_1")});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class, TestClass.class});
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:eyJ2YWx1ZSI6Ik5FV19URVNUX0FSR1VNRU5UIn0=");

        Object[] methodArgs = argumentProcessor.getMethodArgs(servletRequest, TestClass.class, proceedingJoinPoint);

        assertThat(methodArgs, notNullValue());
        assertThat(methodArgs[0], is("TEST_ARGUMENT_1"));
        assertThat(methodArgs[1], is(2));
        TestClass lastArgument = (TestClass) methodArgs[2];
        assertThat(lastArgument, notNullValue());
        assertThat(lastArgument.getValue(), is("NEW_TEST_ARGUMENT"));
    }

    @Test
    public void shouldGetMethodArgsWithNewArgumentsWhenUnknownProperties() {
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[]{"TEST_ARGUMENT_1", 2, new TestClass("JSON_ARGUMENT_1")});
        when(methodSignature.getParameterTypes()).thenReturn(new Class[]{String.class, Integer.class, TestClass.class});
        when(servletRequest.getHeader(OTPJwtValidator.header())).thenReturn("1111:eyJ2YWx1ZSI6Ik5FV19URVNUX0FSR1VNRU5UIiwgInZhbHVlVW5rbm93biI6ICJVTktOT1dOX1RFU1RfQVJHVU1FTlQifQ==");

        Object[] methodArgs = argumentProcessor.getMethodArgs(servletRequest, TestClass.class, proceedingJoinPoint);

        assertThat(methodArgs, notNullValue());
        assertThat(methodArgs[0], is("TEST_ARGUMENT_1"));
        assertThat(methodArgs[1], is(2));
        TestClass lastArgument = (TestClass) methodArgs[2];
        assertThat(lastArgument, notNullValue());
        assertThat(lastArgument.getValue(), is("NEW_TEST_ARGUMENT"));
    }

    static class TestClass {
        private String value;

        public TestClass(String value) {
            this.value = value;
        }

        public TestClass() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}