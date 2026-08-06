package gr.grodov.grsso.service;

import gr.grodov.grsso.api.dto.request.OAuthClientChangeStatusRequest;
import gr.grodov.grsso.api.dto.request.OAuthClientRequest;
import gr.grodov.grsso.api.dto.response.OAuthClientSecretInfoResponse;
import gr.grodov.grsso.domain.dto.OAuthClientDto;
import gr.grodov.grsso.domain.dto.OAuthClientShortDto;
import gr.grodov.grsso.domain.entities.oauth.*;
import gr.grodov.grsso.domain.mapper.Mapper;
import gr.grodov.grsso.domain.repo.OAuthClientRepo;
import gr.grodov.grsso.service.exceptions.OAuthClientNameExistsException;
import gr.grodov.grsso.service.exceptions.OAuthClientNotFoundException;
import gr.grodov.grsso.service.utils.IDGenerator;
import gr.grodov.grsso.service.utils.OAuthClientSettingsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final Mapper<OAuthClient, OAuthClientShortDto> oAuthClientShortMapper;

    @Transactional(readOnly = true)
    public List<OAuthClientShortDto> list() {
        return oAuthClientRepo.findAllByOrderByUpdatedAtDesc().stream().map(oAuthClientShortMapper::fromDB).toList();
    }

    @Transactional
    public OAuthClientSecretInfoResponse save(OAuthClientRequest clientInfo) {
        if (oAuthClientRepo.existsByClientName(clientInfo.getClientName())) {
            throw new OAuthClientNameExistsException();
        }

        String clientID = IDGenerator.randomID(clientInfo.getClientName());
        String clientSecret = UUID.randomUUID().toString();

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
            .clientSettings(OAuthClientSettings.builder().build())
            .tokenSettings(OAuthTokenSettings.builder().build())
            .status(OAuthClientStatus.ACTIVE)
        .build();

        oAuthClientRepo.save(client);
        return new OAuthClientSecretInfoResponse(clientID, clientSecret);
    }

    @Transactional
    public OAuthClientDto edit(OAuthClientRequest clientInfo) {
        OAuthClient client = oAuthClientRepo.findById(clientInfo.getId()).orElseThrow(OAuthClientNotFoundException::new);

        client.setClientName(clientInfo.getClientName());
        client.setRedirectUris(clientInfo.getRedirectUris());
        client.setScopes(clientInfo.getScopes());
        client.setAuthorizationGrantTypes(clientInfo.getAuthorizationGrantTypes());

        return oAuthClientMapper.fromDB(oAuthClientRepo.save(client));
    }

    @Transactional(readOnly = true)
    public OAuthClientDto getById(String id) {
        OAuthClient client = oAuthClientRepo.findById(id).orElseThrow(OAuthClientNotFoundException::new);
        return oAuthClientMapper.fromDB(client);
    }

    @Transactional
    public OAuthClientShortDto changeStatus(OAuthClientChangeStatusRequest statusInfo) {
        OAuthClient client = oAuthClientRepo.findById(statusInfo.getId()).orElseThrow(OAuthClientNotFoundException::new);

        client.setStatus(statusInfo.getStatus());
        oAuthClientRepo.save(client);

        return oAuthClientShortMapper.fromDB(client);
    }

    @Transactional
    public void delete(String id) {
        if (!oAuthClientRepo.existsById(id)) {
            throw new OAuthClientNotFoundException();
        }

        oAuthClientRepo.deleteById(id);
    }
}
