package gr.grodov.grsso.security.service;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomOAuthUserService extends DefaultOAuth2UserService {

    @Override
    public OAuth2User loadUser(@NonNull OAuth2UserRequest request) {
        OAuth2User oauthUser = super.loadUser(request);

        oauthUser.toString();
        log.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAaa");
        log.debug(String.format("AAAAAAAAAAAAAAAAAAAAAA %s", oauthUser.toString()));
        // создать пользователя в БД,
        // обновить данные,
        // получить email через GitHub API

        return oauthUser;
    }
}