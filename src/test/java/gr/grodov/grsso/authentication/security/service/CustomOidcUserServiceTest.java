package gr.grodov.grsso.authentication.security.service;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.user.domain.entity.Role;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOidcUserServiceTest {

    @Mock
    private OidcUserRequest oidcUserRequest;
    @Mock
    private OidcUser oidcUser;

    @Mock
    OidcUserService delegate;
    @Mock
    private UserInfoService userInfoService;
    @InjectMocks
    private CustomOidcUserService oidcUserService;

    @Test
    void loadUser_withExistUser_returnUserPrincipal() {
        OidcUserRequest request = buildRequest("google");
        OidcUser oidcUser = mockOidcUser("user@example.com");
        UserInfoDto existingUser = UserInfoDto.builder().id(1L).email("user@test.com").role(Role.USER).build();
        when(delegate.loadUser(request)).thenReturn(oidcUser);
        when(userInfoService.findByEmailAndProvider("user@example.com", AuthProvider.GOOGLE)).thenReturn(existingUser);

        OidcUser result = oidcUserService.loadUser(request);

        assertThat(result).isInstanceOf(UserPrincipal.class);
        verify(userInfoService, never()).createNewUser(any(), any(), any());
    }

    @Test
    void loadUser_withNewUser_returnUserPrincipalAndCreateUser() {
        OidcUserRequest request = buildRequest("google");
        OidcUser oidcUser = mockOidcUser("user@example.com");
        UserInfoDto user = UserInfoDto.builder().id(1L).email("user@example.com").role(Role.USER).build();
        when(delegate.loadUser(request)).thenReturn(oidcUser);
        when(userInfoService.findByEmailAndProvider("user@example.com", AuthProvider.GOOGLE)).thenThrow(UsernameNotFoundException.class);
        when(userInfoService.createNewUser("user@example.com", null, AuthProvider.GOOGLE)).thenReturn(user);

        OidcUser result = oidcUserService.loadUser(request);

        assertThat(result).isInstanceOf(UserPrincipal.class);
        verify(userInfoService).createNewUser("user@example.com", null, AuthProvider.GOOGLE);
    }

    @Test
    void loadUser_withUnknownProvider_throwOAuth2AuthenticationException() {
        OidcUserRequest request = buildRequest("unknown");

        assertThatThrownBy(() -> oidcUserService.loadUser(request))
            .isInstanceOf(OAuth2AuthenticationException.class)
            .satisfies(ex -> {
                var exception = (OAuth2AuthenticationException) ex;
                assertThat(exception.getError().getErrorCode()).isEqualTo("unknown_provider");
            });
    }

    private OidcUserRequest buildRequest(String registrationId) {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
            .clientId("client-id")
            .clientSecret("client-secret")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:9090/login/oauth2/code/" + registrationId)
            .authorizationUri("http://provider/authorize")
            .tokenUri("http://provider/token")
            .userInfoUri("http://provider/userinfo")
            .userNameAttributeName("sub")
            .jwkSetUri("http://provider/jwks")
            .scope("openid", "email")
            .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
            OAuth2AccessToken.TokenType.BEARER, "token-value", Instant.now(), Instant.now().plusSeconds(300)
        );

        OidcIdToken idToken = new OidcIdToken(
            "id-token-value",
            Instant.now(),
            Instant.now().plusSeconds(300),
            Map.of(IdTokenClaimNames.SUB, "123456")
        );

        return new OidcUserRequest(clientRegistration, accessToken, idToken);
    }

    private OidcUser mockOidcUser(String email) {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn(email);
        return oidcUser;
    }
}