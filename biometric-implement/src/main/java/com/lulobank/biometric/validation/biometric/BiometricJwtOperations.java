package com.lulobank.biometric.validation.biometric;

import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.Map;

public interface BiometricJwtOperations {
    @GET("/authentication-service/biometric/validate")
    Call<BiometricTokenValidationResponse> clientCredentials(@HeaderMap Map<String, String> headers,
            @Query("clientId") String clientId, @Query("transactionType") String transactionType);

    @GET("/authentication-service/biometric/{clientId}/validate")
    Call<BiometricTokenValidationResponse> userCredentials(@HeaderMap Map<String, String> headers,
            @Path("clientId") String clientId, @Query("transactionType") String transactionType);
}