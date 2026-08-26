package gr.grodov.grsso.common.cache;

public interface CacheKeyExpireListener<T> {
    void onEvent(String key);
}
