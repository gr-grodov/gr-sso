package gr.grodov.grsso.infrastructure.cache;

import gr.grodov.grsso.common.cache.CacheEntry;
import gr.grodov.grsso.common.cache.CacheKeyExpireListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.DefaultMessage;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CacheKeyExpireDispatcherTest {

    @Mock
    private TestCacheEntityListener testCacheEntityListener;

    @Test
    void onMessage_withCorrectListener_callOnEvent() {
        var dispatcher = new CacheKeyExpireDispatcher(List.of(testCacheEntityListener));
        var message = new DefaultMessage("channel".getBytes(StandardCharsets.UTF_8), "test-cache:123".getBytes(StandardCharsets.UTF_8));

        dispatcher.onMessage(message, null);

        verify(testCacheEntityListener).onEvent("123");
    }

    @Test
    void onMessage_withCorrectListenerAndLongKey_callOnEvent() {
        var dispatcher = new CacheKeyExpireDispatcher(List.of(testCacheEntityListener));
        var message = new DefaultMessage("channel".getBytes(StandardCharsets.UTF_8), "test-cache:userId:123456".getBytes(StandardCharsets.UTF_8));

        dispatcher.onMessage(message, null);

        verify(testCacheEntityListener).onEvent("userId:123456");
    }

    @Test
    void onMessage_withOtherKey_callOnEvent() {
        var dispatcher = new CacheKeyExpireDispatcher(List.of(testCacheEntityListener));
        var message = new DefaultMessage("channel".getBytes(StandardCharsets.UTF_8), "session-cache:123456".getBytes(StandardCharsets.UTF_8));

        dispatcher.onMessage(message, null);

        verify(testCacheEntityListener, never()).onEvent(anyString());
    }

    @Test
    void onMessage_withIncorrectCacheEntity_throwsIllegalStateException() {
        CacheKeyExpireListener<InvalidCacheEntity> listener = _ -> {};

        assertThatThrownBy(() -> new CacheKeyExpireDispatcher(List.of(listener)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void onMessage_withNotConcreteEntity_throwsIllegalStateException() {
        CacheKeyExpireListener<?> listener = _ -> {};

        assertThatThrownBy(() -> new CacheKeyExpireDispatcher(List.of(listener)))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void onMessage_withEmptyKeyPrefixEntity_throwsIllegalStateException() {
        CacheKeyExpireListener<EmptyKeyPrefixCacheEntity> listener = _ -> {};

        assertThatThrownBy(() -> new CacheKeyExpireDispatcher(List.of(listener)))
            .isInstanceOf(IllegalStateException.class);
    }

    private static class TestCacheEntityListener implements CacheKeyExpireListener<TestCacheEntity> {
        @Override
        public void onEvent(String key) {}
    }

    @CacheEntry(keyPrefix = "test-cache")
    private record TestCacheEntity(String value) { }

    @CacheEntry(keyPrefix = "")
    private record EmptyKeyPrefixCacheEntity(String value) {}

    private record InvalidCacheEntity(String value) {}
}