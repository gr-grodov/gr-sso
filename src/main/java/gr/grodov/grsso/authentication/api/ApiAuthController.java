package gr.grodov.grsso.authentication.api;

import gr.grodov.grsso.authentication.api.dto.request.LoginRequest;
import gr.grodov.grsso.authentication.api.dto.request.RefreshVerifyCodeRequest;
import gr.grodov.grsso.authentication.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.authentication.api.dto.request.VerifyEmailRequest;
import gr.grodov.grsso.authentication.api.dto.response.RegistrationResponse;
import gr.grodov.grsso.authentication.api.dto.response.VerifyEmailResponse;
import gr.grodov.grsso.authentication.service.RegistrationService;
import gr.grodov.grsso.authentication.service.VerifyEmailService;
import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;
    private final VerifyEmailService verifyEmailService;

    @PostMapping("/login")
    public SuccessResponse<Void> login(
        @Valid @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        authenticationService.authenticate(request, httpRequest, httpResponse);
        return SuccessResponse.of(true);
    }

    @PostMapping("/register")
    public RegistrationResponse register(
        @Valid @RequestBody RegistrationRequest request,
        HttpServletRequest httpRequest
    ) {
        return registrationService.registration(request, httpRequest);
    }

    @PostMapping("/verify-email")
    public SuccessResponse<Void> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return SuccessResponse.of(verifyEmailService.verifyEmail(request));
    }

    @DeleteMapping("/cancel-verify-code/{verifyId}")
    public SuccessResponse<Void> cancelVerifyEmail(@PathVariable String verifyId) {
        verifyEmailService.cancelVerifyEmail(verifyId);
        return SuccessResponse.of(true);
    }

    @PostMapping("/refresh-verify-code")
    public SuccessResponse<Void> refreshVerifyCode(@Valid @RequestBody RefreshVerifyCodeRequest request, Locale locale) {
        verifyEmailService.refreshCode(request, locale);
        return SuccessResponse.of(true);
    }
}