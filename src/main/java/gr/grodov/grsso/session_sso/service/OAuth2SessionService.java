package gr.grodov.grsso.session_sso.service;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.common.utils.DeviceContext;
import gr.grodov.grsso.session_sso.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.session_sso.domain.entity.OAuth2Session;
import gr.grodov.grsso.session_sso.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.session_sso.exception.ErrorCreateOAuth2SessionException;
import gr.grodov.grsso.session_sso.exception.OAuth2SessionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Objects;

@NamedInterface("service")
@Service
@RequiredArgsConstructor
public class OAuth2SessionService {

    private final OAuth2SessionRepo sessionRepo;
    private final OAuth2AuthorizationService authorizationService;
    private final RegisteredClientRepository registeredClientRepository;

    @Transactional
    public void createOrUpdateSession(OAuth2Authorization authorization, DeviceContext deviceContext) {
        OAuth2Session session = sessionRepo.findByUserIdAndClientIdAndDeviceId(
            authorization.getPrincipalName(),
            authorization.getRegisteredClientId(),
            deviceContext.deviceId()
        ).orElse(null);

        if (session == null) {
            createSession(authorization, deviceContext);
        } else {
            updateSession(session, authorization);
            cleanPrevAuthorization(session, authorization);
        }
    }

    @Transactional
    public void updateSession(OAuth2Authorization authorization) {
        OAuth2Session session = sessionRepo.findByAuthorizationId(authorization.getId())
            .orElseThrow(OAuth2SessionNotFoundException::new);

        updateSession(session, authorization);
    }

    @Transactional(readOnly = true)
    public String getSID(OAuth2Authorization authorization) {
        return sessionRepo.findByAuthorizationId(authorization.getId()).orElseThrow().getSid().toString();
    }

    private void cleanPrevAuthorization(OAuth2Session session, OAuth2Authorization currentAuthorization) {
        if (Objects.equals(session.getAuthorizationId(), currentAuthorization.getId())) {
            return;
        }

        OAuth2Authorization prevAuthorization = authorizationService.findById(session.getAuthorizationId());
        if (prevAuthorization != null) {
            authorizationService.remove(prevAuthorization);
        }
    }

    private OAuth2Session createSession(OAuth2Authorization authorization, DeviceContext deviceContext) {
        RegisteredClient client = registeredClientRepository.findById(authorization.getRegisteredClientId());
        if (client == null) {
            throw new ErrorCreateOAuth2SessionException();
        }

        return sessionRepo.save(OAuth2Session.builder()
            .authorizationId(authorization.getId())
            .userId(Long.valueOf(authorization.getPrincipalName()))
            .clientId(authorization.getRegisteredClientId())
            .clientName(client.getClientName())
            .deviceId(deviceContext.deviceId())
            .deviceIpAddress(deviceContext.deviceIpAddress())
            .deviceUserAgent(deviceContext.deviceUserAgent())
            .lastUsedAt(Instant.now())
        .build());
    }

    private void updateSession(OAuth2Session session, OAuth2Authorization currentAuthorization) {
        sessionRepo.updateAuthorization(session.getSid(), currentAuthorization.getId());
    }
}
