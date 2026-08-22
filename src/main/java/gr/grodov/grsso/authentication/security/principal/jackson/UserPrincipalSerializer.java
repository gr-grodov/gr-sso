package gr.grodov.grsso.authentication.security.principal.jackson;

import gr.grodov.grsso.authentication.security.principal.UserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;

public class UserPrincipalSerializer extends ValueSerializer<UserPrincipal> {

    @Override
    public void serialize(UserPrincipal principal, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
        log();
        gen.writeStartObject();

        gen.writeNumberProperty("id", principal.getId());
        gen.writeStringProperty("email", principal.getEmail());
        gen.writeStringProperty("provider", principal.getProvider().name());

        gen.writeArrayPropertyStart("authorities");

        for (GrantedAuthority authority : principal.getAuthorities()) {
            gen.writeString(authority.getAuthority());
        }

        gen.writeEndArray();
        gen.writeEndObject();
    }


    @Override
    public void serializeWithType(UserPrincipal value, JsonGenerator gen, SerializationContext context, TypeSerializer typeSerializer) throws JacksonException {
        log();
        WritableTypeId typeId = typeSerializer.writeTypePrefix(
            gen, context, typeSerializer.typeId(value, JsonToken.START_OBJECT)
        );

        gen.writeNumberProperty("id", value.getId());
        gen.writeStringProperty("email", value.getEmail());
        gen.writeStringProperty("provider", value.getProvider().name());

        gen.writeArrayPropertyStart("authorities");
        for (GrantedAuthority authority : value.getAuthorities()) {
            gen.writeString(authority.getAuthority());
        }
        gen.writeEndArray();

        typeSerializer.writeTypeSuffix(gen, context, typeId);
    }

    private void log() {
        try {
            throw new Exception();
        } catch (Exception e) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> LOG ");
            e.printStackTrace();
        }
    }
}