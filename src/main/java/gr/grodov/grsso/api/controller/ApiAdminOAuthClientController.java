package gr.grodov.grsso.api.controller;

import gr.grodov.grsso.api.dto.request.OAuthClientChangeStatusRequest;
import gr.grodov.grsso.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.api.dto.response.SuccessResponse;
import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthAuthorizationGrantType;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.domain.entities.oauth_client.OAuthScope;
import gr.grodov.grsso.service.OAuthClientsService;
import gr.grodov.grsso.service.OAuthPropertiesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/oauth-client")
public class ApiAdminOAuthClientController {

    private final OAuthClientsService oAuthClientsService;
    private final OAuthPropertiesService oAuthPropertiesService;

    @PostMapping
    public OAuthClientSecretInfoResponse create(@Valid @RequestBody OAuthClientRequest oAuthClient) {
        return oAuthClientsService.save(oAuthClient);
    }

    @PatchMapping
    public OAuthClientDto patch(@Valid @RequestBody OAuthClientRequest oAuthClient) {
        return oAuthClientsService.edit(oAuthClient);
    }

    @DeleteMapping("/{id}")
    public SuccessResponse<Void> delete(@PathVariable String id) {
        oAuthClientsService.delete(id);
        return SuccessResponse.of(true);
    }

    @GetMapping("/list")
    public List<OAuthClientShortDto> list() throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.list();
    }

    @GetMapping("/{id}")
    public OAuthClientDto get(@PathVariable String id) throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.getById(id);
    }

    @GetMapping("/search")
    public OAuthClientDto search(@RequestParam(required = false) String clientId) throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.getByClientId(clientId);
    }

    @PatchMapping("/status")
    public OAuthClientShortDto changeStatus(@Valid @RequestBody OAuthClientChangeStatusRequest statusInfo) {
        return oAuthClientsService.changeStatus(statusInfo);
    }

    @GetMapping("/scopes")
    public OAuthScope[] scopes() {
        return OAuthScope.values();
    }

    @GetMapping("/auth-grant-types")
    public List<OAuthAuthorizationGrantType> grantType() {
        return oAuthPropertiesService.authorizationGrantTypes().stream().toList();
    }

    @GetMapping("/auth-methods")
    public List<OAuthClientAuthenticationMethod> authenticationMethods() {
        return oAuthPropertiesService.authenticationMethods().stream().toList();
    }
}
