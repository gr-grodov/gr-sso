package gr.grodov.grsso.oauth_session.service;

import gr.grodov.grsso.common.event.OAuthLogoutEvent;
import gr.grodov.grsso.oauth_session.api.dto.response.UsersOAuth2SessionResponse;
import gr.grodov.grsso.oauth_session.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserIdentificationProjection;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserOAuth2SessionProjection;
import gr.grodov.grsso.oauth_session.service.dto.UserOAuth2SessionDto;
import gr.grodov.grsso.oauth_session.service.dto.UserOAuth2SessionDto.OAuthClientShortInfo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserOAuth2SessionService {
    private final OAuth2SessionRepo sessionRepo;
    private final ApplicationEventPublisher publisher;

    @Transactional(readOnly = true)
    public UsersOAuth2SessionResponse search(@Nullable String search, Pageable pageable) {
        Page<UserIdentificationProjection> users = search != null
            ? sessionRepo.searchUsers(search, pageable)
            : sessionRepo.findAllUsers(pageable);

        if (users.isEmpty()) {
            return new UsersOAuth2SessionResponse(List.of(), 0, 0);
        }

        Map<UUID, List<OAuthClientShortInfo>> usersSessions = getUsersSessions(
            users.stream().map(UserIdentificationProjection::getUserId).toList()
        );

        List<UserOAuth2SessionDto> usersWithSessions = users.stream()
            .map(user ->new UserOAuth2SessionDto(
                user.getUserId(),
                user.getUserEmail(),
                user.getUserAvatarId(),
                usersSessions.get(user.getUserId())
            ))
            .toList();
        return new UsersOAuth2SessionResponse(usersWithSessions, users.getTotalElements(), users.getTotalPages());
    }

    @Transactional(readOnly = true)
    public void logoutSessionsFromClient(String clientId, UUID userId) {
        List<UUID> sids = sessionRepo.findSessionsForUserClient(userId, clientId).stream()
            .map(UserOAuth2SessionProjection::getSid)
            .toList();
        publisher.publishEvent(new OAuthLogoutEvent(sids, userId));
    }

    private Map<UUID, List<OAuthClientShortInfo>> getUsersSessions(List<UUID> userIds) {
        return sessionRepo.findSessionsForUsers(userIds)
            .stream()
            .collect(Collectors.groupingBy(
                UserOAuth2SessionProjection::getUserId,
                Collectors.collectingAndThen(Collectors.toList(), this::toClientInfo)
            ));
    }

    private List<OAuthClientShortInfo> toClientInfo(List<UserOAuth2SessionProjection> sessions) {
        return sessions.stream()
            .collect(Collectors.groupingBy(UserOAuth2SessionProjection::getClientId))
            .values()
            .stream()
            .map(clientSessions -> new OAuthClientShortInfo(
                clientSessions.getFirst().getClientId(),
                clientSessions.getFirst().getClientName(),
                clientSessions.getFirst().getClientAvatarId(),
                clientSessions.size()
            ))
            .toList();
    }
}
