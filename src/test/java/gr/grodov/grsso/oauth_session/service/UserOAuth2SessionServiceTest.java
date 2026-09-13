package gr.grodov.grsso.oauth_session.service;

import gr.grodov.grsso.common.event.OAuthLogoutEvent;
import gr.grodov.grsso.oauth_session.domain.repo.OAuth2SessionRepo;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserIdentificationProjection;
import gr.grodov.grsso.oauth_session.domain.repo.projection.UserOAuth2SessionProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserOAuth2SessionServiceTest {

    private final static UUID USER_ID = UUID.randomUUID();

    @Autowired
    @Mock
    private OAuth2SessionRepo sessionRepo;
    @Mock
    private ApplicationEventPublisher publisher;
    @InjectMocks
    private UserOAuth2SessionService sessionService;

    @Test
    void search_emptyUsers_returnEmptyUsers() {
        when(sessionRepo.findAllUsers(any())).thenReturn(Page.empty());

        var result = sessionService.search(null, Pageable.ofSize(10));

        assertThat(result.users()).isEqualTo(List.of());
        assertThat(result.countUsers()).isEqualTo(0L);
        assertThat(result.totalPage()).isEqualTo(0);
    }

    @Test
    void search_withOneClientAndOneSession_returnUsers() {
        when(sessionRepo.findAllUsers(any())).thenReturn(new PageImpl<>(List.of(buildUserIdentification())));
        when(sessionRepo.findSessionsForUsers(List.of(USER_ID))).thenReturn(List.of(buildUserOAuth2Session("123")));

        var result = sessionService.search(null,  Pageable.ofSize(10));

        assertThat(result.users().size()).isEqualTo(1);
        assertThat(result.users().getFirst()).satisfies(user -> {
            assertThat(user.userId()).isEqualTo(USER_ID);
            assertThat(user.userEmail()).isEqualTo("user@example.com");
            assertThat(user.clients().size()).isEqualTo(1);
            assertThat(user.clients().getFirst().countSessions()).isEqualTo(1);
            assertThat(user.clients().getFirst().clientId()).isEqualTo("123");
        });
        assertThat(result.totalPage()).isEqualTo(1);
        assertThat(result.countUsers()).isEqualTo(1);
    }

    @Test
    void search_withOneClientAndManySessions_returnUsers() {
        when(sessionRepo.findAllUsers(any())).thenReturn(new PageImpl<>(List.of(buildUserIdentification())));
        when(sessionRepo.findSessionsForUsers(List.of(USER_ID))).thenReturn(List.of(
            buildUserOAuth2Session("1"),
            buildUserOAuth2Session("1"),
            buildUserOAuth2Session("1")
        ));

        var result = sessionService.search(null,  Pageable.ofSize(10));

        assertThat(result.users().size()).isEqualTo(1);
        assertThat(result.users().getFirst()).satisfies(user -> {
            assertThat(user.userId()).isEqualTo(USER_ID);
            assertThat(user.userEmail()).isEqualTo("user@example.com");
            assertThat(user.clients().size()).isEqualTo(1);
            assertThat(user.clients().getFirst().countSessions()).isEqualTo(3);
        });
        assertThat(result.totalPage()).isEqualTo(1);
        assertThat(result.countUsers()).isEqualTo(1);
    }

    @Test
    void search_withManyClientAndOneSession_returnUsers() {
        when(sessionRepo.findAllUsers(any())).thenReturn(new PageImpl<>(List.of(buildUserIdentification())));
        when(sessionRepo.findSessionsForUsers(List.of(USER_ID))).thenReturn(List.of(
            buildUserOAuth2Session("1"),
            buildUserOAuth2Session("2"),
            buildUserOAuth2Session("3")
        ));

        var result = sessionService.search(null,  Pageable.ofSize(10));

        assertThat(result.users().size()).isEqualTo(1);
        assertThat(result.users().getFirst()).satisfies(user -> {
            assertThat(user.userId()).isEqualTo(USER_ID);
            assertThat(user.userEmail()).isEqualTo("user@example.com");
            assertThat(user.clients().size()).isEqualTo(3);
            user.clients().forEach(client -> {
                assertThat(client.countSessions()).isEqualTo(1);
            });
        });
        assertThat(result.totalPage()).isEqualTo(1);
        assertThat(result.countUsers()).isEqualTo(1);
    }

    @Test
    void search_withManyClientAndManySessions_returnUsers() {
        when(sessionRepo.findAllUsers(any())).thenReturn(new PageImpl<>(List.of(buildUserIdentification())));
        when(sessionRepo.findSessionsForUsers(List.of(USER_ID))).thenReturn(List.of(
            buildUserOAuth2Session("1"),
            buildUserOAuth2Session("1"),
            buildUserOAuth2Session("2"),
            buildUserOAuth2Session("2")
        ));

        var result = sessionService.search(null,  Pageable.ofSize(10));

        assertThat(result.users().size()).isEqualTo(1);
        assertThat(result.users().getFirst()).satisfies(user -> {
            assertThat(user.userId()).isEqualTo(USER_ID);
            assertThat(user.userEmail()).isEqualTo("user@example.com");
            assertThat(user.clients().size()).isEqualTo(2);
            user.clients().forEach(client -> {
                assertThat(client.countSessions()).isEqualTo(2);
            });
        });
        assertThat(result.totalPage()).isEqualTo(1);
        assertThat(result.countUsers()).isEqualTo(1);
    }

    @Test
    void logoutSessionsFromClient_withCorrect_publishEvent() {
        UserOAuth2SessionProjection userSession = buildUserOAuth2Session("1");
        var expectSID = userSession.getSid();
        when(sessionRepo.findSessionsForUserClient(USER_ID, "1")).thenReturn(List.of(userSession));

        sessionService.logoutSessionsFromClient("1", USER_ID);

        ArgumentCaptor<OAuthLogoutEvent> logoutEvent = ArgumentCaptor.forClass(OAuthLogoutEvent.class);
        verify(publisher).publishEvent(logoutEvent.capture());
        assertThat(logoutEvent.getValue()).isNotNull().satisfies(event -> {
            assertThat(event.sids()).isEqualTo(List.of(expectSID));
            assertThat(event.userId()).isEqualTo(USER_ID);
        });
    }

    private UserIdentificationProjection buildUserIdentification() {
        return new UserIdentificationProjection() {
            @Override
            public UUID getUserId() {
                return USER_ID;
            }

            @Override
            public String getUserEmail() {
                return "user@example.com";
            }
        };
    }

    private UserOAuth2SessionProjection buildUserOAuth2Session(String clientId) {
        return new UserOAuth2SessionProjection() {
            private final UUID sid = UUID.randomUUID();
            @Override
            public UUID getUserId() {
                return USER_ID;
            }

            @Override
            public UUID getSid() {
                return sid;
            }

            @Override
            public String getClientId() {
                return clientId;
            }

            @Override
            public String getClientName() {
                return "client_%s".formatted(clientId);
            }

            @Override
            public Instant getLastUsedAt() {
                return Instant.now();
            }
        };
    }
}