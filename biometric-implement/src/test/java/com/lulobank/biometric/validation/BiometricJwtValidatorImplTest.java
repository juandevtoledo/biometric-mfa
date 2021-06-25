package com.lulobank.biometric.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.validation.biometric.BiometricJwtOperations;
import com.lulobank.biometric.validation.biometric.BiometricJwtValidatorImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.lulobank.biometric.validation.MockConstant.*;
import static com.lulobank.biometric.validation.Sample.biometricTokenValidationRequest;
import static com.lulobank.biometric.validation.Sample.biometricTokenValidationResponse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

public class BiometricJwtValidatorImplTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private BiometricJwtValidatorImpl biometricJWTValidatorImpl;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8981);

    @Before
    public void setUp() {
        BiometricJwtOperations retrofitBiometricJWTOperations = RetrofitFactory.create("http://localhost:8981", BiometricJwtOperations.class);
        biometricJWTValidatorImpl = new BiometricJwtValidatorImpl(retrofitBiometricJWTOperations);
    }

    @Test
    public void userCredentials() throws JsonProcessingException {
        String responseStr = mapper.writeValueAsString(biometricTokenValidationResponse(SUCCESSFUL_RESPONSE));
        wireMockRule.stubFor(WireMock.get(urlEqualTo("/authentication-service/biometric/" + ID_CLIENT + "/validate"))
            .willReturn(aResponse()
            .withStatus(HttpStatus.OK.value())
            .withBody(responseStr)));
        BiometricTokenValidationRequest request = biometricTokenValidationRequest();
        BiometricTokenValidationResponse response = biometricJWTValidatorImpl.validateWithUserCredentials(new HashMap<>(), request);
        assertThat(response.getContent().getStatus(), is(SUCCESSFUL_RESPONSE));
    }

    @Test(expected = BiometricUnauthorizedException.class)
    public void invalidBioToken() throws JsonProcessingException {
        String responseStr = mapper.writeValueAsString(biometricTokenValidationResponse(ERROR_RESPONSE));
        wireMockRule.stubFor(WireMock.get(urlEqualTo("/authentication-service/biometric/" + ID_CLIENT + "/validate"))
            .willReturn(aResponse()
            .withStatus(HttpStatus.PRECONDITION_FAILED.value())
            .withBody(responseStr)));
        BiometricTokenValidationRequest request = biometricTokenValidationRequest();
        BiometricTokenValidationResponse response = biometricJWTValidatorImpl.validateWithUserCredentials(new HashMap<>(), request);
        assertThat(response.getContent().getStatus(), is(ERROR_RESPONSE));
    }

    @Test
    public void clientCredentials() throws JsonProcessingException {
        String responseStr = mapper.writeValueAsString(biometricTokenValidationResponse(SUCCESSFUL_RESPONSE));
        wireMockRule.stubFor(get(urlMatching("/authentication-service/biometric/validate\\?.*")).willReturn(
            aResponse()
            .withStatus(200)
            .withBody(responseStr)
            .withHeader("Content-Type", CONTENT_TYPE)));
        BiometricTokenValidationRequest request = biometricTokenValidationRequest();
        BiometricTokenValidationResponse response = biometricJWTValidatorImpl.validateWithClientCredentials(new HashMap<>(), request);
        assertThat(response.getContent().getStatus(), is(SUCCESSFUL_RESPONSE));
    }
}