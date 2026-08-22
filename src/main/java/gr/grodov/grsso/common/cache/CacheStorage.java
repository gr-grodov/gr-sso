package gr.grodov.grsso.common.cache;

public interface CacheStorage<T> {
    void save(String key, T request);

    T get(String key);

    void delete(String key);
}
