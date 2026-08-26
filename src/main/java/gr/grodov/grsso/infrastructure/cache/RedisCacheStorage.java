package gr.grodov.grsso.infrastructure.cache;

import gr.grodov.grsso.common.cache.CacheEntry;
import gr.grodov.grsso.common.cache.CacheStorage;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.SetCondition;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Optional;

public class RedisCacheStorage<T> implements CacheStorage<T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final RedisSerializer<String> keySerializer;
    private final RedisSerializer<T> valueSerializer;
    private final long ttlSeconds;

    public RedisCacheStorage(RedisConnectionFactory connectionFactory, Class<T> type) {
        this.keySerializer = new StringRedisSerializer();
        this.valueSerializer = new JacksonJsonRedisSerializer<>(type);

        this.redisTemplate = buildTemplate(connectionFactory);
        this.ttlSeconds = type.getAnnotation(CacheEntry.class).ttlSeconds();
    }

    private RedisTemplate<String, T> buildTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, T> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(this.keySerializer);
        template.setValueSerializer(this.valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Override
    public void save(String key, T entity) {
        redisTemplate.opsForValue().set(key, entity, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void save(String key, T entity, Duration duration) {
        redisTemplate.opsForValue().set(key, entity, duration);
    }

    @Override
    public boolean update(String key, T entity) {
        return redisTemplate.execute((RedisCallback<Boolean>) conn -> conn.stringCommands().set(
            keySerializer.serialize(key),
            valueSerializer.serialize(entity),
            SetCondition.ifPresent(),
            Expiration.keepTtl()
        ));
    }

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}