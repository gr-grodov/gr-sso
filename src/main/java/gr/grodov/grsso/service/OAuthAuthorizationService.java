package gr.grodov.grsso.service;

import gr.grodov.grsso.cache.storage.CacheStorage;
import gr.grodov.grsso.cache.storage.OAuthRequestStorage;
import gr.grodov.grsso.cache.storage.dto.OAuthAuthorizationRequest;
import gr.grodov.grsso.service.exceptions.OAuthAuthorizationGoneException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthAuthorizationService {
    private final CacheStorage<OAuthAuthorizationRequest> oAuthRequestStorage;

    public String saveRequest(String request) {
        OAuthAuthorizationRequest authRequest = new OAuthAuthorizationRequest(request, Instant.now());
        String code = UUID.randomUUID().toString();

        oAuthRequestStorage.save(code, authRequest);
        return code;
    }

    public String getSavedRequest(String code) {
        OAuthAuthorizationRequest request = oAuthRequestStorage.get(code);
        if (request == null) {
            return null;
        }

        oAuthRequestStorage.delete(code);
        return request.requestUri();
    }
}
