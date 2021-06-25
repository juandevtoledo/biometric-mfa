package com.lulobank.biometric.exceptionhandler.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorMFA {
    private String failure;
    private String code;
    private String detail;


    public static ErrorMFA unAuthorizedBiometricToken(String detail){
        return ErrorMFA.builder()
                .failure(detail)
                .code(GeneralErrorStatus.GEN_043.name())
                .detail(GeneralErrorStatus.DEFAULT_DETAIL)
                .build();
    }

}
