package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.LoginRequest;
import gr.grodov.grsso.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.security.service.UserPrincipal;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserInfoService userInfoService;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/user-info")
    public UserInfoDto getUserInfo(@AuthenticationPrincipal UserPrincipal principal) {
        return userInfoService.findByUserInfo(principal.getName(), principal.getProvider());
    }

    @PostMapping("/login")
    public SuccessResponse<Void> login(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication authResult = authenticationManager.authenticate(authRequest);
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, httpRequest, httpResponse);

        return SuccessResponse.of(true);
    }

    @PostMapping("/register")
    public SuccessResponse<Void> register(@Valid @RequestBody RegistrationRequest request) {
        userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL);
        return SuccessResponse.of(true);
    }
}
