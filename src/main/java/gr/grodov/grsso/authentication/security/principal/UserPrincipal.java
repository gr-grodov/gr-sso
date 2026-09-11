package gr.grodov.grsso.authentication.security.principal;

import gr.grodov.grsso.common.security.AuthPrincipal;
import gr.grodov.grsso.user.domain.dto.UserInfoDto;
import gr.grodov.grsso.user.domain.entity.AuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OidcUser, AuthPrincipal {

    private final UUID id;
    private final String email;
    private final String password;
    private final AuthProvider provider;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public String getName() {
        return id.toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes == null ? Map.of() : attributes;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public Map<String, Object> getClaims() {
        if (idToken != null) {
            return idToken.getClaims();
        }

        return Map.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    public static UserPrincipal local(UserInfoDto user) {
        return UserPrincipal.builder()
            .id(user.id())
            .email(user.email())
            .password(user.password())
            .provider(user.provider())
            .authorities(List.of(user.role()))
            .attributes(Map.of())
        .build();
    }

    public static UserPrincipal oidc(UserInfoDto user, OidcUser oidcUser) {
        return UserPrincipal.builder()
            .id(user.id())
            .email(user.email())
            .provider(user.provider())
            .authorities(List.of(user.role()))
            .attributes(oidcUser.getAttributes())
            .idToken(oidcUser.getIdToken())
            .userInfo(oidcUser.getUserInfo())
        .build();
    }
}
