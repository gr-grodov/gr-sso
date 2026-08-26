package gr.grodov.grsso.common.cache;

import java.time.Duration;
import java.util.Optional;

public interface CacheStorage<T> {
    void save(String key, T entity);
    void save(String key, T entity, Duration duration);
    boolean update(String key, T entity);
    Optional<T> get(String key);
    boolean delete(String key);
}
