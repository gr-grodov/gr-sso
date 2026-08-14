package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.LoginRequest;
import gr.grodov.grsso.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.security.service.auth.AuthenticationService;
import gr.grodov.grsso.security.principal.UserPrincipal;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserInfoService userInfoService;
    private final AuthenticationService authenticationService;

    @GetMapping("/user-info")
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserPrincipal principal) {
        return userInfoService.findById(principal.getName());
    }

    @PostMapping("/login")
    public SuccessResponse<Void> login(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        authenticationService.authenticate(request, httpRequest, httpResponse);
        return SuccessResponse.of(true);
    }

    @PostMapping("/register")
    public SuccessResponse<Void> register(@Valid @RequestBody RegistrationRequest request) {
        userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL);
        return SuccessResponse.of(true);
    }
}
