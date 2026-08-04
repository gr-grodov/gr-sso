package gr.grodov.grsso.security.service;

import gr.grodov.grsso.domain.dto.UserInfoDto;
import gr.grodov.grsso.domain.entities.user.AuthProvider;
import gr.grodov.grsso.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserInfoService userInfoService;

    @Override
    public OidcUser loadUser(@NonNull OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(request);

        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(request.getClientRegistration().getRegistrationId().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unknown_provider"));
        }

        UserInfoDto userInfo;
        try {
            userInfo = userInfoService.findByUserInfo(oidcUser.getEmail(), provider);
        } catch (UsernameNotFoundException e) {
            userInfo = userInfoService.createNewUser(oidcUser.getEmail(), null, provider);
        }

        return UserPrincipal.oidc(userInfo, oidcUser);
    }
}
