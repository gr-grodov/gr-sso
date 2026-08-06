package gr.grodov.grsso.domain.converter;

import org.springframework.boot.jackson.ObjectValueDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

public class SecondsToDurationDeserializer extends ObjectValueDeserializer<Duration> {

    @Override
    protected Duration deserializeObject(JsonParser jsonParser, DeserializationContext context, JsonNode tree) {
        return Duration.ofSeconds(jsonParser.getLongValue());
    }
}