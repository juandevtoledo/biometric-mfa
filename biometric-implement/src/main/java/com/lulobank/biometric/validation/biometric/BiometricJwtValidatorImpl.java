package com.lulobank.biometric.validation.biometric;

import com.lulobank.biometric.api.validation.biometric.exception.BiometricUnauthorizedException;
import com.lulobank.biometric.api.validation.biometric.BiometricJwtValidator;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationRequest;
import com.lulobank.biometric.api.validation.biometric.dto.BiometricTokenValidationResponse;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class BiometricJwtValidatorImpl implements BiometricJwtValidator {
    private final BiometricJwtOperations biometricJWTAuthentication;

    @Override
    public BiometricTokenValidationResponse validateWithClientCredentials(Map<String, String> headers,
            BiometricTokenValidationRequest tokenValidationRequest) {
        return process(headers, tokenValidationRequest, () -> biometricJWTAuthentication.clientCredentials(headers,
                tokenValidationRequest.getClientId(), tokenValidationRequest.getTransactionType()));
    }

    @Override
    public BiometricTokenValidationResponse validateWithUserCredentials(Map<String, String> headers,
            BiometricTokenValidationRequest tokenValidationRequest) {
        return process(headers, tokenValidationRequest, () -> biometricJWTAuthentication.userCredentials(headers,
                tokenValidationRequest.getClientId(), tokenValidationRequest.getTransactionType()));
    }

    private BiometricTokenValidationResponse process(Map<String, String> headers,
            BiometricTokenValidationRequest tokenValidationRequest,
            CheckedFunction0<Call<BiometricTokenValidationResponse>> validation) {
        headers.put(BiometricJwtValidator.header(),
                tokenValidationRequest.getBiometricToken());
        return Try.of(validation)
            .mapTry(Call::execute)
            .mapTry(this::processResponse)
            .transform(this::getOrHandleErrors);
    }

    private <T> T processResponse(Response<T> retrofitResponse) {
        if (retrofitResponse.isSuccessful() && retrofitResponse.body() != null) return retrofitResponse.body();
        String errorBody = Try.of(retrofitResponse::errorBody)
            .mapTry(ResponseBody::string)
            .filter(errorText -> !StringUtils.isEmpty(errorText))
            .getOrElse("Request failed");
        throw new BiometricUnauthorizedException(String.format("STATUS CODE: %s. %s", retrofitResponse.code(), errorBody));
    }

    private <T> T getOrHandleErrors(Try<T> responseTry) {
        return responseTry
            .onFailure(e -> log.error("Failure while processing the request", e))
            .getOrElseThrow(e -> new BiometricUnauthorizedException("UNAUTHORIZED_MFA_BIOMETRIC"));
    }
}
