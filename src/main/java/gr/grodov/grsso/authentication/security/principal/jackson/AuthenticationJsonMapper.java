package gr.grodov.grsso.authentication.security.principal.jackson;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import gr.grodov.grsso.common.jackson.SpecificJsonMapper;
import gr.grodov.grsso.user.domain.entity.Role;
import org.springframework.security.jackson.SecurityJacksonModules;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Component("authentication_json_mapper")
public class AuthenticationJsonMapper extends SpecificJsonMapper {

    protected AuthenticationJsonMapper() {
        ClassLoader classLoader = AuthenticationJsonMapper.class.getClassLoader();
        BasicPolymorphicTypeValidator.Builder validator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(UserPrincipal.class)
            .allowIfSubType(Role.class);

        JsonMapper mapper = JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(classLoader, validator))
            .addModule(new UserPrincipalJacksonModule())
            .build();

        super(mapper);
    }
}
