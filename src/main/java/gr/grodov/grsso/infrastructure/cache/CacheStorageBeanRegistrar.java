package gr.grodov.grsso.infrastructure.cache;

import gr.grodov.grsso.common.cache.CacheEntry;
import gr.grodov.grsso.common.cache.CacheStorage;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Component
public class CacheStorageBeanRegistrar implements BeanDefinitionRegistryPostProcessor {

    private static final String BASE_PACKAGE = "gr.grodov.grsso";
    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CacheEntry.class));

        Set<String> entries = new HashSet<>();
        scanner.findCandidateComponents(BASE_PACKAGE).forEach(candidate -> {
            Class<?> entryClass = ClassUtils.resolveClassName(
                Objects.requireNonNull(candidate.getBeanClassName()), getClass().getClassLoader()
            );
            registrationCacheStorageBean(registry, entryClass, entries);
        });
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void registrationCacheStorageBean(BeanDefinitionRegistry registry, Class<?> entryClass, Set<String> entries) {
        String beanName = beanNameCacheStorage(entryClass, entries);

        RootBeanDefinition definition = new RootBeanDefinition();
        definition.setTargetType(ResolvableType.forClassWithGenerics(CacheStorage.class, entryClass));
        definition.setInstanceSupplier(() -> new RedisCacheStorage<>(
            beanFactory.getBean(RedisConnectionFactory.class),
            entryClass
        ));

        registry.registerBeanDefinition(beanName, definition);
    }

    private String beanNameCacheStorage(Class<?> entryClass, Set<String> entries) {
        String beanName = "%sCacheStorage".formatted(ClassUtils.getShortNameAsProperty(entryClass));
        if (!entries.add(beanName)) {
            beanName = "%sCacheStorage".formatted(entryClass.getName().replace('.', '_'));
            entries.add(beanName);
        }
        return beanName;
    }
}
