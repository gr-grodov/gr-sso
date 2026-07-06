package gr.grodov.grsso.security.service;

import gr.grodov.grsso.domain.entities.AuthProvider;
import gr.grodov.grsso.domain.repo.UserInfoRepo;
import gr.grodov.grsso.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final UserInfoService userInfoService;

    @Override
    public OidcUser loadUser(@NonNull OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(request);
        userInfoService.createNewUser(user.getEmail(), null, AuthProvider.GOOGLE);

        return user;
    }
}
