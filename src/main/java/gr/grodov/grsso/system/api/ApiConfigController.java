package gr.grodov.grsso.system.api;

import gr.grodov.grsso.common.api.SuccessResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config")
public class ApiConfigController {
    @GetMapping("/status")
    public SuccessResponse<Void> status() {
        return SuccessResponse.of(true);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
