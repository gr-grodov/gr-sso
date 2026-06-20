package gr.grodov.grsso.controller;

import gr.grodov.grsso.controller.dto.RegistrationRequest;
import gr.grodov.grsso.service.UserInfoService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PermitAll
@RequestMapping("/register")
public class RegistrationController {

    private final UserInfoService userInfoService;

    @PostMapping
    public ResponseEntity<String> registration(@RequestBody RegistrationRequest request) {
        userInfoService.createNewUser(request.email(), request.password());
        return ResponseEntity.ok("ok");
    }
}
