package gr.grodov.grsso.cache.storage;

import gr.grodov.grsso.cache.storage.dto.OAuthAuthorizationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OAuthRequestStorage implements CacheStorage<OAuthAuthorizationRequest>{

    private final RedisTemplate<String, OAuthAuthorizationRequest> redis;

    public void save(String key, OAuthAuthorizationRequest request) {
        redis.opsForValue().set(key, request, Duration.ofMinutes(5));
    }

    public OAuthAuthorizationRequest get(String key) {
        return redis.opsForValue().get(key);
    }

    public void delete(String key) {
        redis.delete(key);
    }
}