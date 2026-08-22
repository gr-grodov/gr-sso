package gr.grodov.grsso.authentication.security.principal.jackson;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import tools.jackson.databind.module.SimpleModule;

public class UserPrincipalJacksonModule extends SimpleModule {

    public UserPrincipalJacksonModule() {
        super("UserPrincipalJacksonModule");

        addSerializer(UserPrincipal.class, new UserPrincipalSerializer());
        addDeserializer(UserPrincipal.class, new UserPrincipalDeserializer());
    }
}