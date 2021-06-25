package com.lulobank.biometric.validation;

import com.lulobank.biometric.api.annotation.MFA;
import com.lulobank.biometric.api.annotation.SecurityType;
import com.lulobank.biometric.api.validation.MFAJwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.lulobank.biometric.util.Constant.CLIENT_ID_PARAM;

@Slf4j
@RequiredArgsConstructor
public abstract class MFAJwtHandler<R, T> {
    private final MFAJwtValidator<R, T> mfaJwtValidator;

    public void validate(HttpServletRequest httpServletRequest, MFA annotation) {
        SecurityType securityType = annotation.securityType();
        if (securityType.equals(SecurityType.CLIENT_CREDENTIALS)) {
            processResponse(mfaJwtValidator.validateWithClientCredentials(fillRequestHeaders(httpServletRequest),
                    buildValidationRequest(httpServletRequest, annotation)));
        } else if (securityType.equals(SecurityType.USER_CREDENTIALS)) {
            processResponse(mfaJwtValidator.validateWithUserCredentials(fillRequestHeaders(httpServletRequest),
                    buildValidationRequest(httpServletRequest, annotation)));
        }
    }

    protected String retrieveClientFromRequest(HttpServletRequest httpServletRequest) {
        Map<String, String> pathVariables = (Map<String, String>) httpServletRequest.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return pathVariables.get(CLIENT_ID_PARAM);
    }

    protected abstract Map<String, String> fillRequestHeaders(HttpServletRequest httpServletRequest);

    protected abstract R buildValidationRequest(HttpServletRequest httpServletRequest, MFA annotation);

    protected abstract void processResponse(T tokenValidationResponse);
}
