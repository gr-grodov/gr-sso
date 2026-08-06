package gr.grodov.grsso.cache.storage;

import gr.grodov.grsso.cache.storage.dto.OAuthAuthorizationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public interface CacheStorage<T> {
    void save(String key, T request);

    T get(String key);

    void delete(String key);
}
