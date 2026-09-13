package gr.grodov.grsso.authorization_sso.security;

import gr.grodov.grsso.authorization_sso.exception.OAuthPrincipalNotFoundException;
import gr.grodov.grsso.user.service.dto.UserInfoDto;
import gr.grodov.grsso.user.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthClaimsServiceTest {

    private final static UUID USER_ID = UUID.randomUUID();

    @Mock
    private OAuth2TokenContext tokenContext;
    @Mock
    private OidcUserInfoAuthenticationContext oidcContext;
    @Mock
    private OAuth2Authorization auth2Authorization;
    @Mock
    private Authentication authentication;

    @Autowired
    @Mock
    private UserInfoService userInfoService;
    @InjectMocks
    private OAuthClaimsService oAuthClaimsService;

    @Test
    void tokenClaims_withAccessToken_returnClaimsWithOnlySub() {
        UserInfoDto userInfo = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();
        when(tokenContext.getPrincipal()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_ID.toString());
        when(userInfoService.findById(USER_ID.toString())).thenReturn(userInfo);
        when(tokenContext.getTokenType()).thenReturn(OAuth2TokenType.ACCESS_TOKEN);

        var result = oAuthClaimsService.tokenClaims(tokenContext);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void tokenClaims_withRefreshToken_returnClaims() {
        UserInfoDto userInfo = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();
        when(tokenContext.getPrincipal()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_ID.toString());
        when(userInfoService.findById(USER_ID.toString())).thenReturn(userInfo);
        when(tokenContext.getTokenType()).thenReturn(OAuth2TokenType.REFRESH_TOKEN);
        when(tokenContext.getAuthorizedScopes()).thenReturn(Set.of("openid", "email"));

        var result = oAuthClaimsService.tokenClaims(tokenContext);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void tokenClaims_withoutAuthentication_throwsOAuthPrincipalNotFoundException() {
        when(tokenContext.getPrincipal()).thenReturn(null);

        assertThatThrownBy(() -> oAuthClaimsService.tokenClaims(tokenContext))
            .isExactlyInstanceOf(OAuthPrincipalNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthPrincipalNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("user_error_loading");
            });
        verifyNoInteractions(userInfoService);
    }

    @Test
    void userInfoClaims_withToken_returnClaims() {
        UserInfoDto userInfo = UserInfoDto.builder().id(USER_ID).email("test@test.com").build();
        when(oidcContext.getAuthorization()).thenReturn(auth2Authorization);
        when(auth2Authorization.getAttribute(Principal.class.getName())).thenReturn(authentication);
        when(authentication.getName()).thenReturn(USER_ID.toString());
        when(userInfoService.findById(USER_ID.toString())).thenReturn(userInfo);
        when(auth2Authorization.getAuthorizedScopes()).thenReturn(Set.of("openid", "email"));

        var result = oAuthClaimsService.userInfoClaims(oidcContext);

        assertThat(result.get(StandardClaimNames.SUB)).isEqualTo(USER_ID.toString());
        assertThat(result.get(StandardClaimNames.EMAIL)).isEqualTo("test@test.com");
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void userInfoClaims_withoutAuthentication_throwsOAuthPrincipalNotFoundException() {
        when(oidcContext.getAuthorization()).thenReturn(auth2Authorization);
        when(auth2Authorization.getAttribute(Principal.class.getName())).thenReturn(null);

        assertThatThrownBy(() -> oAuthClaimsService.userInfoClaims(oidcContext))
            .isExactlyInstanceOf(OAuthPrincipalNotFoundException.class)
            .satisfies(ex -> {
                var exception = (OAuthPrincipalNotFoundException) ex;
                assertThat(exception.getCode()).isEqualTo("user_error_loading");
            });
        verifyNoInteractions(userInfoService);
    }
}