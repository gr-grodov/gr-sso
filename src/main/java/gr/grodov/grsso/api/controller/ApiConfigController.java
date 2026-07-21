package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.response.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ApiConfigController {
    @GetMapping("/status")
    public ResponseEntity<SuccessResponse<Void>> status() {
        return ResponseEntity.ok(SuccessResponse.of(true));
    }

    @GetMapping("/csrf")
    public ResponseEntity<CsrfToken> csrf(CsrfToken token) {
        return ResponseEntity.ok(token);
    }
}
