package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.OAuth2ConsentRequest;
import gr.grodov.grsso.api.dto.response.RedirectURIResponse;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.service.OAuth2FlowService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth2")
public class OAuth2FlowController {

    private final OAuth2FlowService oAuth2FlowService;

    @GetMapping("/continue")
    public RedirectURIResponse continueFlow() {
        String redirectURI = oAuth2FlowService.getSavedRedirectRequest();
        return new RedirectURIResponse(redirectURI);
    }

    @PostMapping("/consent")
    public SuccessResponse<Void> consent(
        @RequestBody OAuth2ConsentRequest consentRequest,
        HttpServletRequest httpRequest
    ) {
        oAuth2FlowService.sendOAuthConsent(consentRequest, httpRequest);
        return SuccessResponse.of(true);
    }
}
