package com.lulobank.biometric.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import com.lulobank.biometric.validation.otp.OtpJwtOperations;
import com.lulobank.biometric.validation.otp.OtpJwtValidatorImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.lulobank.biometric.validation.MockConstant.ID_CLIENT;
import static com.lulobank.biometric.validation.MockConstant.OTP_TOKEN;
import static com.lulobank.biometric.validation.MockConstant.OTP_TOKEN_VALUE;
import static com.lulobank.biometric.validation.MockConstant.TRANSACTION_TYPE;
import static com.lulobank.biometric.validation.Sample.otpTokenValidationRequest;
import static com.lulobank.biometric.validation.Sample.otpTokenValidationResponse;
import static org.junit.Assert.assertTrue;

public class OtpJwtValidatorImplTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private OtpJwtValidatorImpl otpJwtValidatorImpl;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    private Map<String, String> headers;

    @Before
    public void setUp() {
        OtpJwtOperations otpJwtOperations = RetrofitFactory.create("http://localhost:8981",
                OtpJwtOperations.class);
        otpJwtValidatorImpl = new OtpJwtValidatorImpl(otpJwtOperations);
        headers = new HashMap<>();
        headers.put(OTP_TOKEN, OTP_TOKEN_VALUE);
    }

    @Test
    public void userCredentials() throws JsonProcessingException {
        String responseStr = mapper.writeValueAsString(otpTokenValidationResponse(true));
        wireMockRule.stubFor(WireMock.post(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withBody(responseStr)));
        OTPTokenValidationRequest request = otpTokenValidationRequest();
        OTPTokenValidationResponse response = otpJwtValidatorImpl.validateWithUserCredentials(headers, request);
        assertTrue(response.isValid());
        verify(postRequestedFor(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .withHeader(OTP_TOKEN, equalTo(OTP_TOKEN_VALUE)));
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void userCredentials_invalidToken()  {
        wireMockRule.stubFor(WireMock.post(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.FORBIDDEN.value())));
        OTPTokenValidationRequest request = otpTokenValidationRequest();
        OTPTokenValidationResponse response = otpJwtValidatorImpl.validateWithUserCredentials(headers, request);
        verify(postRequestedFor(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .withHeader(OTP_TOKEN, equalTo(OTP_TOKEN_VALUE)));
    }

    @Test(expected = OTPUnauthorizedException.class)
    public void userCredentials_otpNotFound()  {
        wireMockRule.stubFor(WireMock.post(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));
        OTPTokenValidationRequest request = otpTokenValidationRequest();
        OTPTokenValidationResponse response = otpJwtValidatorImpl.validateWithUserCredentials(headers, request);
        verify(postRequestedFor(urlEqualTo("/otp/v3/" + ID_CLIENT + "/verify/checksum/" + TRANSACTION_TYPE))
                .withHeader(OTP_TOKEN, equalTo(OTP_TOKEN_VALUE)));
    }

    @Test(expected = NotImplementedException.class)
    public void clientCredentials() {
        OTPTokenValidationRequest request = otpTokenValidationRequest();
        OTPTokenValidationResponse response = otpJwtValidatorImpl.validateWithClientCredentials(new HashMap<>(), request);
    }
}