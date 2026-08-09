package gr.grodov.grsso.cache.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import gr.grodov.grsso.cache.storage.dto.OAuthAuthorizationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.jackson.SecurityJacksonModules;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisConfig {

   /* @Bean("redisMapper")
    JsonMapper oauthAuthorizationJsonMapper() {

        ClassLoader classLoader = getClass().getClassLoader();
        BasicPolymorphicTypeValidator.Builder validator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(OAuthAuthorizationRequest.class);

        return JsonMapper.builder()
            .addModules(SecurityJacksonModules.getModules(classLoader, validator))
            .build();
    }

*//*    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration("server", 6379));
    }*/


    @Bean
    public RedisTemplate<String, OAuthAuthorizationRequest> redisTemplate(
        RedisConnectionFactory factory
    ) {
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(OAuthAuthorizationRequest.class)
            .build();

        JsonMapper objectMapper = JsonMapper.builder()
            .activateDefaultTyping(validator, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .build();

        RedisTemplate<String, OAuthAuthorizationRequest> template = new RedisTemplate<>();
        RedisSerializer<Object> valueSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);
        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        template.setConnectionFactory(factory);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        return template;
    }

}