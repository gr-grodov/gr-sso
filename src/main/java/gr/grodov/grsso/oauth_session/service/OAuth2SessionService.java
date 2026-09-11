package gr.grodov.grsso.oauth_session.service;

import gr.grodov.grsso.common.mapper.Mapper;
import gr.grodov.grsso.oauth_session.domain.dto.OAuth2SessionDto;
import gr.grodov.grsso.oauth_session.service.dto.DeviceContext;
import gr.grodov.grsso.oauth_session.domain.entity.OAuth2Session;
import gr.grodov.grsso.oauth_session.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.oauth_session.exception.ErrorCreateOAuth2SessionException;
import gr.grodov.grsso.oauth_session.exception.OAuth2SessionNotFoundException;
import gr.grodov.grsso.oauth_session.service.dto.GeoLocation;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@NamedInterface("service")
@Service
@RequiredArgsConstructor
public class OAuth2SessionService {

    private final OAuth2SessionRepo sessionRepo;
    private final Mapper<OAuth2Session, OAuth2SessionDto> sessionMapper;
    private final GeoLocationResolverService geoLocationResolver;
    private final OAuth2AuthorizationService authorizationService;
    private final RegisteredClientRepository registeredClientRepository;

    @Transactional(readOnly = true)
    public List<OAuth2SessionDto> list(String userId, String deviceId) {
         return sessionRepo.findAllByUserId(UUID.fromString(userId)).stream()
            .map(session -> {
                OAuth2SessionDto sessionDto = sessionMapper.fromDB(session);
                return sessionDto.withCurrentDeviceFlag(session.getDeviceId().equals(deviceId));
            })
            .toList();
    }

    @Transactional
    public void createOrUpdateSession(OAuth2Authorization authorization, DeviceContext deviceContext) {
        OAuth2Session session = sessionRepo.findByUserIdAndClientIdAndDeviceId(
            UUID.fromString(authorization.getPrincipalName()),
            authorization.getRegisteredClientId(),
            deviceContext.deviceId()
        ).orElse(null);

        if (session == null) {
            createSession(authorization, deviceContext);
        } else {
            String prevAuthorizationId = session.getAuthorizationId();
            updateSession(session, authorization, deviceContext);
            cleanPrevAuthorization(authorization, prevAuthorizationId);
        }
    }

    @Transactional
    public void updateSession(OAuth2Authorization authorization) {
        OAuth2Session session = sessionRepo.findByAuthorizationId(authorization.getId())
            .orElseThrow(OAuth2SessionNotFoundException::new);

        updateSession(session, authorization, null);
    }

    @Transactional(readOnly = true)
    public String getSID(OAuth2Authorization authorization) {
        return sessionRepo.findByAuthorizationId(authorization.getId())
            .orElseThrow(OAuth2SessionNotFoundException::new)
            .getSid().toString();
    }

    @Transactional(readOnly = true)
    public OAuth2SessionDto getSessionBySID(String sid, String userId) {
        return sessionMapper.fromDB(sessionRepo.findBySidAndUserId(UUID.fromString(sid), userId)
            .orElseThrow(OAuth2SessionNotFoundException::new));
    }

    @Transactional
    public void deleteSession(String sid) {
        sessionRepo.deleteById(UUID.fromString(sid));
    }

    private OAuth2Session createSession(OAuth2Authorization authorization, DeviceContext deviceContext) {
        RegisteredClient client = registeredClientRepository.findById(authorization.getRegisteredClientId());
        if (client == null) {
            throw new ErrorCreateOAuth2SessionException();
        }

        GeoLocation location = geoLocationResolver.resolve(deviceContext.deviceIpAddress());
        return sessionRepo.save(OAuth2Session.builder()
            .authorizationId(authorization.getId())
            .userId(UUID.fromString(authorization.getPrincipalName()))
            .clientId(authorization.getRegisteredClientId())
            .clientName(client.getClientName())
            .deviceId(deviceContext.deviceId())
            .deviceIpAddress(deviceContext.deviceIpAddress())
            .deviceLocationCountry(location.country())
            .deviceLocationCity(location.city())
            .deviceUserAgent(deviceContext.deviceUserAgent())
            .deviceType(deviceContext.deviceType())
            .lastUsedAt(Instant.now())
        .build());
    }

    private void updateSession(OAuth2Session session, OAuth2Authorization currentAuthorization, @Nullable DeviceContext deviceContext) {
        if (deviceContext == null) {
            sessionRepo.updateAuthorization(session.getSid(), Instant.now(), currentAuthorization.getId());
            return;
        }

        GeoLocation location = geoLocationResolver.resolve(deviceContext.deviceIpAddress());
        session.setAuthorizationId(currentAuthorization.getId());
        session.setDeviceIpAddress(deviceContext.deviceIpAddress());
        session.setDeviceLocationCountry(location.country());
        session.setDeviceLocationCity(location.city());
        session.setDeviceUserAgent(deviceContext.deviceUserAgent());
        session.setDeviceType(deviceContext.deviceType());
        session.setLastUsedAt(Instant.now());
        sessionRepo.saveAndFlush(session);
    }

    private void cleanPrevAuthorization(OAuth2Authorization currentAuthorization, String prevAuthorizationId) {
        if (Objects.equals(prevAuthorizationId, currentAuthorization.getId())) {
            return;
        }

        OAuth2Authorization prevAuthorization = authorizationService.findById(prevAuthorizationId);
        if (prevAuthorization != null) {
            authorizationService.remove(prevAuthorization);
        }
    }
}
