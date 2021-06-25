package com.lulobank.biometric.validation.otp;

import com.lulobank.biometric.api.validation.otp.OTPJwtValidator;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationRequest;
import com.lulobank.biometric.api.validation.otp.dto.OTPTokenValidationResponse;
import com.lulobank.biometric.api.validation.otp.exception.OTPUnauthorizedException;
import io.vavr.CheckedFunction0;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OtpJwtValidatorImpl implements OTPJwtValidator {

    private final OtpJwtOperations otpJwtOperations;

    @Override
    public OTPTokenValidationResponse validateWithUserCredentials(Map<String, String> headers,
                                                                  OTPTokenValidationRequest tokenValidationRequest) {
        return process(() -> otpJwtOperations.userCredentials(headers,
                tokenValidationRequest.getClientId(), tokenValidationRequest.getTransactionType()),
                tokenValidationRequest);
    }

    @Override
    public OTPTokenValidationResponse validateWithClientCredentials(Map<String, String> headers,
                                                                    OTPTokenValidationRequest tokenValidationRequest) {
        throw new NotImplementedException();
    }

    private OTPTokenValidationResponse process(CheckedFunction0<Call<OTPTokenValidationResponse>> validation,
                                               OTPTokenValidationRequest tokenValidationRequest) {
        return Try.of(validation)
                .mapTry(Call::execute)
                .mapTry(this::processResponse)
                .transform(response -> getOrHandleErrors(response, tokenValidationRequest));
    }

    private <T> T processResponse(Response<T> retrofitResponse) {
        return Option.of(retrofitResponse)
                .filter(Response::isSuccessful)
                .filter(response -> response.body() != null)
                .map(Response::body)
                .getOrElse(() -> getError(retrofitResponse));
    }

    private <T> T getError(Response<T> retrofitResponse) {
        String errorBody = Try.of(retrofitResponse::errorBody)
                .mapTry(ResponseBody::string)
                .filter(errorText -> !StringUtils.isEmpty(errorText))
                .getOrElse("Request failed");
        throw new OTPUnauthorizedException(String.format("STATUS CODE: %s. %s", retrofitResponse.code(), errorBody));
    }

    private <T> T getOrHandleErrors(Try<T> responseTry, OTPTokenValidationRequest tokenValidationRequest) {
        return responseTry
                .onFailure(e -> log.error("Failure while processing the request validate otp. idClient {}, Error {}.",
                        tokenValidationRequest.getClientId(), e.getMessage(), e))
                .getOrElseThrow(e -> new OTPUnauthorizedException("UNAUTHORIZED_MFA_OTP"));
    }


}
