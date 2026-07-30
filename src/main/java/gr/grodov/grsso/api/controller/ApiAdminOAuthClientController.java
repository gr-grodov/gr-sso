package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.service.OAuthClientsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/oauth-client")
public class ApiAdminOAuthClientController {

    private final OAuthClientsService oAuthClientsService;

    @PostMapping
    public OAuthClientSecretInfoResponse create(@RequestBody OAuthClientRequest oAuthClient) {
        return oAuthClientsService.save(oAuthClient);
    }
}
