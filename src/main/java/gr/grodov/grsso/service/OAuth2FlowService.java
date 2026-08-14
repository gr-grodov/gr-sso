package gr.grodov.grsso.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2FlowService {

    private static final String SESSION_ATTR_REDIRECT_URI = "OAUTH_REDIRECT_URI";

    private final SessionService sessionService;

    public void saveRedirectRequest(String request) {
        sessionService.setAttribute(SESSION_ATTR_REDIRECT_URI, request);
    }

    public String getSavedRedirectRequest() {
        String redirectRequest = sessionService.getAttribute(SESSION_ATTR_REDIRECT_URI, String.class);
        sessionService.removeAttribute(SESSION_ATTR_REDIRECT_URI);
        return redirectRequest;
    }
}
