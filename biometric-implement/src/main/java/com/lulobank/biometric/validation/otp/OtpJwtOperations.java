package com.lulobank.biometric.validation.otp;

import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import retrofit2.Call;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface OtpJwtOperations {

    @POST("/otp/v3/{idClient}/verify/checksum/{transactionType}")
    Call<OTPTokenValidationResponse> userCredentials(@HeaderMap Map<String, String> headers,
                                                     @Path("idClient") String idClient,
                                                     @Path("transactionType") String transactionType);
}