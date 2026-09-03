package gr.grodov.grsso.oauth_client.api;

import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientChangeStatusRequest;
import gr.grodov.grsso.oauth_client.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.oauth_client.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.common.api.SuccessResponse;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientDto;
import gr.grodov.grsso.oauth_client.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthAuthorizationGrantType;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.oauth_client.domain.entity.OAuthScope;
import gr.grodov.grsso.oauth_client.service.OAuthClientsService;
import gr.grodov.grsso.oauth_client.service.OAuthClientPropertiesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/oauth-client")
public class ApiAdminOAuthClientController {

    private final OAuthClientsService oAuthClientsService;
    private final OAuthClientPropertiesService oAuthClientPropertiesService;

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
    public List<OAuthClientDto> list() throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.list();
    }

    @GetMapping("/get")
    public OAuthClientDto get(@RequestParam(required = false) String id) throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.getById(id);
    }

    @GetMapping("/search")
    public OAuthClientDto search(@RequestParam(required = false) String clientId) throws InterruptedException {
        Thread.sleep(2000);
        return oAuthClientsService.getByClientId(clientId);
    }

    @PatchMapping("/status")
    public OAuthClientDto changeStatus(@Valid @RequestBody OAuthClientChangeStatusRequest statusInfo) {
        return oAuthClientsService.changeStatus(statusInfo);
    }

    @GetMapping("/scopes")
    public OAuthScope[] scopes() {
        return OAuthScope.values();
    }

    @GetMapping("/auth-grant-types")
    public List<OAuthAuthorizationGrantType> grantType() {
        return oAuthClientPropertiesService.authorizationGrantTypes().stream().toList();
    }

    @GetMapping("/auth-methods")
    public List<OAuthClientAuthenticationMethod> authenticationMethods() {
        return oAuthClientPropertiesService.authenticationMethods().stream().toList();
    }
}
