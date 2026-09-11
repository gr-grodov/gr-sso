package gr.grodov.grsso.authorization_sso.api;

import gr.grodov.grsso.authorization_sso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.authorization_sso.api.dto.response.RedirectURIResponse;
import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.authorization_sso.service.OAuth2ConsentService;
import gr.grodov.grsso.authorization_sso.service.OAuth2FlowService;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2FlowController {

    private final OAuth2FlowService oAuth2FlowService;
    private final OAuth2ConsentService oAuth2ConsentService;

    private final OAuthClientsService oAuthClientsService;

    @GetMapping("/continue")
    public RedirectURIResponse continueFlow() {
        String redirectURI = oAuth2FlowService.getSavedRedirectRequest();
        return new RedirectURIResponse(redirectURI);
    }

    @PostMapping("/consent")
    public SuccessResponse<Void> consent(
        @Valid @RequestBody OAuth2ConsentRequest consentRequest,
        HttpServletRequest httpRequest
    ) {
        String redirectUri = oAuth2ConsentService.requestAuthorizationRedirect(consentRequest, httpRequest);
        oAuth2FlowService.saveRedirectRequest(redirectUri);

        return SuccessResponse.of(true);
    }

    @GetMapping("/client/{clientId}")
    public OAuthClientShortDto get(@PathVariable String clientId) throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.getShortInfoClient(clientId);
    }
}
