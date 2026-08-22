package gr.grodov.grsso.authentication.security.principal.jackson;

import gr.grodov.grsso.user.domain.entity.AuthProvider;
import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.jackson.ObjectValueDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;

public class UserPrincipalDeserializer extends ObjectValueDeserializer<UserPrincipal> {

    @Override
    protected UserPrincipal deserializeObject(
        @NonNull JsonParser jsonParser,
        @NonNull DeserializationContext context,
        JsonNode json
    ) {
        Long id = json.get("id").asLong();
        String email = json.get("email").asString();
        AuthProvider provider = AuthProvider.valueOf(json.get("provider").asString());

        List<GrantedAuthority> authorities = new ArrayList<>();
        JsonNode authoritiesNode = json.get("authorities");

        if (authoritiesNode != null && authoritiesNode.isArray()) {
            for (JsonNode authorityNode : authoritiesNode) {

                String authority = authorityNode.asString();
                authorities.add(() -> authority);
            }
        }

        return new UserPrincipal(
            id,
            email,
            null,
            provider,
            authorities,
            Map.of(),
            null,
            null
        );
    }
}