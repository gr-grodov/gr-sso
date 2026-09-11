package gr.grodov.grsso.authorization_sso.api;

import gr.grodov.grsso.authorization_sso.service.BackChannelLogoutService;
import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.common.security.AuthPrincipal;
import gr.grodov.grsso.oauth_session.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.service.OAuth2SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2-session")
public class OAuth2SessionController {

    private final OAuth2SessionService sessionService;
    private final BackChannelLogoutService logoutService;

    @GetMapping("/list")
    public List<OAuth2SessionDto> list(
        Principal principal,
        @CookieValue(name = "device_id", required = false) String currentDeviceId
    ) throws InterruptedException {
        Thread.sleep(2000);
        return sessionService.list(principal.getName(), currentDeviceId);
    }

    @DeleteMapping("/{sid}")
    public SuccessResponse<Void> delete(@PathVariable String sid, @AuthenticationPrincipal AuthPrincipal principal) {
        logoutService.logoutFromClient(sid, principal.getId());
        return SuccessResponse.of(true);
    }
}
