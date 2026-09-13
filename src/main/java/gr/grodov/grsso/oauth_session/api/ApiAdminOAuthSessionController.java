package gr.grodov.grsso.oauth_session.api;

import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.oauth_session.api.dto.request.DeleteOAuth2SessionRequest;
import gr.grodov.grsso.oauth_session.api.dto.response.UsersOAuth2SessionResponse;
import gr.grodov.grsso.oauth_session.service.UserOAuth2SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/oauth-session")
public class ApiAdminOAuthSessionController {

    private final UserOAuth2SessionService sessionService;

    @GetMapping("/search")
    public UsersOAuth2SessionResponse search(
        @RequestParam(required = false) String search,
        @PageableDefault(size = 8) Pageable page
    ) {
        return sessionService.search(search, page);
    }

    @DeleteMapping
    public SuccessResponse<Void> delete(@RequestBody @Valid DeleteOAuth2SessionRequest deleteRequest) {
        sessionService.logoutSessionsFromClient(deleteRequest.clientId(), deleteRequest.userId());
        return SuccessResponse.of(true);
    }
}
