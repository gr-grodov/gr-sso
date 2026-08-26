package gr.grodov.grsso.common.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CacheEntry {
    long ttlSeconds() default 900;
}
