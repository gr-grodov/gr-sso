package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.LoginRequest;
import gr.grodov.grsso.api.dto.request.RegistrationRequest;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.security.exceptions.EmailAlreadyExistsException;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ApiAuthController {

    private final UserInfoService userInfoService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/a")
    public ResponseEntity<SuccessResponse<Void>> a() {
        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok(userInfoService.findByEmail(authentication.getName()));
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<Void>> login(@RequestBody LoginRequest request, Authentication authentication) {
        Authentication auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(
                request.getEmail(),
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(auth);


        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        userInfoService.createNewUser(request.getEmail(), request.getPassword(), AuthProvider.LOCAL);
        return ResponseEntity.ok(SuccessResponse.of(true));
    }
}
