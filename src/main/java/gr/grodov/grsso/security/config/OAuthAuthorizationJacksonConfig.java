package gr.grodov.grsso.security.config;

import gr.grodov.grsso.security.jackson.UserPrincipalJacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jackson.SecurityJacksonModules;
import tools.jackson.databind.json.JsonMapper;

public class OAuthAuthorizationJacksonConfig {

    public JsonMapper oauthAuthorizationJsonMapper() {

        ClassLoader classLoader =
                getClass().getClassLoader();

        return JsonMapper.builder()
                .addModules(
                        SecurityJacksonModules.getModules(classLoader)
                )
                .addModule(
                        new UserPrincipalJacksonModule()
                )
                .build();
    }
}