package gr.grodov.grsso.infrastructure.cache;

import gr.grodov.grsso.common.cache.CacheEntry;
import gr.grodov.grsso.common.cache.CacheKeyExpireListener;
import org.jspecify.annotations.Nullable;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CacheKeyExpireDispatcher implements MessageListener {

    private final Map<String, CacheKeyExpireListener<?>> namespaceListeners;

    public CacheKeyExpireDispatcher(List<CacheKeyExpireListener<?>> listeners) {
        this.namespaceListeners = listeners.stream().collect(
            Collectors.toMap(this::namespaceByListener, Function.identity())
        );
    }

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String expiredKey = new String(message.getBody(), StandardCharsets.UTF_8);
        int separatorNamespace = expiredKey.indexOf(':');

        String namespace = expiredKey.substring(0, separatorNamespace);
        CacheKeyExpireListener<?> listener = namespaceListeners.get(namespace);
        if (listener == null) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>> not message with key: %s".formatted(expiredKey));
            return;
        }
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> publish key: %s to listener: %s".formatted(expiredKey, listener.getClass().getName()));
        String logicalKey = expiredKey.substring(separatorNamespace + 1);
        listener.onEvent(logicalKey);
    }

    private String namespaceByListener(CacheKeyExpireListener<?> listener) {
        Class<?> entityType = ResolvableType
            .forClass(listener.getClass())
            .as(CacheKeyExpireListener.class)
            .getGeneric(0)
            .resolve();

        if (entityType == null) {
            throw new IllegalStateException("Cannot resolve generic type for %s, — must implement CacheKeyExpireListener<T> directly with a concrete type"
                .formatted(listener.getClass().getName())
            );
        }

        CacheEntry annotation = entityType.getAnnotation(CacheEntry.class);
        if (annotation == null) {
            throw new IllegalStateException("Cannot resolve generic type for %s, — type must have annotation: %s"
                .formatted(listener.getClass().getName(), CacheEntry.class.getName())
            );
        }

        String namespace = annotation.keyPrefix();
        if (namespace.isBlank()) {
            throw new IllegalStateException("Key Prefix must not blank %s".formatted(annotation.getClass().getName()));
        }
        return namespace;
    }
}
