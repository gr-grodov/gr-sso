package gr.grodov.grsso.infrastructure.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisExpireKetListenerContainer(
        RedisConnectionFactory connectionFactory,
        CacheKeyExpireDispatcher cacheKeyExpireDispatcher
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(
            cacheKeyExpireDispatcher,
            new PatternTopic("__keyevent@0__:expired") // 0 — номер Redis DB
        );
        return container;
    }
}
