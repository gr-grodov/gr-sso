package gr.grodov.grsso.domain.converter;


import org.springframework.boot.jackson.ObjectValueSerializer;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

import java.time.Duration;

public class DurationToSecondsSerializer extends ObjectValueSerializer<Duration> {

    @Override
    protected void serializeObject(Duration value, JsonGenerator jgen, SerializationContext context) {
        jgen.writeNumber(value.getSeconds());
    }
}