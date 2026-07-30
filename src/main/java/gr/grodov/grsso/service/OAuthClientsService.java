package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.entities.oauth.OAuthClient;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientAuthenticationMethod;
import gr.grodov.grsso.domain.entities.oauth.OAuthClientStatus;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.OAuthClientRepo;
import gr.grodov.grsso.service.exceptions.OAuthClientNameExistsException;
import gr.grodov.grsso.service.utils.IDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthClientsService {

    private final PasswordEncoder passwordEncoder;
    private final OAuthClientRepo oAuthClientRepo;
    private final Mapper<OAuthClient, OAuthClientDto> oAuthClientMapper;

    @Transactional(readOnly = true)
    public List<OAuthClientDto> list() {
        return oAuthClientRepo.findAll().stream().map(oAuthClientMapper::fromDB).toList();
    }

    @Transactional
    public OAuthClientSecretInfoResponse save(OAuthClientRequest clientInfo) {
        if (oAuthClientRepo.existsByClientName(clientInfo.getClientName())) {
            throw new OAuthClientNameExistsException("oauth_client_name_inlavid", "oauth_client.clientName", "exists");
        }

        String clientID = getClientID(clientInfo.getClientName());
        String clientSecret = getClientSecret();

        OAuthClient client = OAuthClient.builder()
            .clientName(clientInfo.getClientName())
            .redirectUris(clientInfo.getRedirectUris())
            .scopes(clientInfo.getScopes())
            .authorizationGrantTypes(clientInfo.getAuthorizationGrantTypes())

            .id(UUID.randomUUID().toString())
            .clientId(clientID)
            .clientIdIssuedAt(Instant.now())
            .clientSecret(passwordEncoder.encode(clientSecret))
            .clientAuthenticationMethods(Set.of(OAuthClientAuthenticationMethod.CLIENT_SECRET_BASIC))
            .clientSettings(Map.of())
            .tokenSettings(Map.of())
            .status(OAuthClientStatus.ACTIVE)
        .build();

        oAuthClientRepo.save(client);
        return new OAuthClientSecretInfoResponse(clientID, clientSecret);
    }



    private String getClientID(String name) {
        return String.format("%s_%s",
            name.toLowerCase().replace(" ", "-"),
            IDGenerator.randomID()
        );
    }

    private String getClientSecret() {
        return UUID.randomUUID().toString();
    }
}
