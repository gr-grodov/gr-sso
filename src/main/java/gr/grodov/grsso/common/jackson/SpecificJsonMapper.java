package gr.grodov.grsso.common.jackson;

import lombok.Getter;
import tools.jackson.databind.json.JsonMapper;

@Getter
public abstract class SpecificJsonMapper {
    private final JsonMapper jsonMapper;

    protected SpecificJsonMapper(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }
}
