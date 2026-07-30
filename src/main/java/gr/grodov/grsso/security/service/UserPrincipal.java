package gr.grodov.grsso.security.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
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

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements UserDetails, OidcUser {

    private final Long id;
    private final String email;
    private final String password;
    private final AuthProvider provider;
    private final Collection<? extends GrantedAuthority> authorities;

    private final Map<String, Object> attributes;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getName() {
        return email;
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
        return new UserPrincipal(
            user.id(),
            user.email(),
            user.password(),
            user.provider(),
            List.of(user.role()),
            Map.of(),
            null,
            null
        );
    }

    public static UserPrincipal oidc(UserInfoDto user, OidcUser oidcUser) {
        return new UserPrincipal(
            user.id(),
            user.email(),
            null,
            user.provider(),
            List.of(user.role()),
            oidcUser.getAttributes(),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo()
        );
    }
}
