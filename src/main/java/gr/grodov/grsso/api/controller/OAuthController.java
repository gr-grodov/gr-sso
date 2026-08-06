package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.service.OAuthAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class OAuthController {

    private final OAuthAuthorizationService oAuthAuthorizationService;

    @GetMapping("/continue")
    public ResponseEntity<Void> continueAuthorization(@RequestParam String code) {
        String oAuthRequest = oAuthAuthorizationService.getSavedRequest(code);
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(oAuthRequest))
            .build();
    }

}
