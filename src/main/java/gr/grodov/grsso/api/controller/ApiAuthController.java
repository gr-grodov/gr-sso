package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.LoginRequest;
import gr.grodov.grsso.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.security.service.AuthenticationService;
import gr.grodov.grsso.security.service.SessionService;
import gr.grodov.grsso.security.service.UserPrincipal;
import gr.grodov.grsso.service.OAuthAuthorizationService;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserInfoService userInfoService;
    private final SessionService sessionService;
    private final AuthenticationService authenticationService;
    private final OAuthAuthorizationService oAuthAuthorizationService;

    @GetMapping("/user-info")
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserPrincipal principal) {
        return userInfoService.findByUserInfo(principal.getName(), principal.getProvider());
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Void>> login(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        authenticationService.authenticate(request, httpRequest, httpResponse);

        String oauthCode = sessionService.getAttribute(SessionService.Attributes.OAUTH_FLOW, String.class);
        if (oauthCode != null) {
            String oAuthRequest = oAuthAuthorizationService.getSavedRequest(oauthCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(oAuthRequest))
                .build();
        }

        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @PostMapping("/register")
    public SuccessResponse<Void> register(@Valid @RequestBody RegistrationRequest request) {
        userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL);
        return SuccessResponse.of(true);
    }
}
