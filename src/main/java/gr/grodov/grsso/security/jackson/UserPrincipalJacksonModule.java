package gr.grodov.grsso.security.jackson;

import gr.grodov.grsso.security.service.UserPrincipal;
import tools.jackson.databind.module.SimpleModule;

public class UserPrincipalJacksonModule
        extends SimpleModule {

    public UserPrincipalJacksonModule() {

        super("UserPrincipalJacksonModule");

        addSerializer(
                UserPrincipal.class,
                new UserPrincipalSerializer()
        );

        addDeserializer(
                UserPrincipal.class,
                new UserPrincipalDeserializer()
        );
    }
}