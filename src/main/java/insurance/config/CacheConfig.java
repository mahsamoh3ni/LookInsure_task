package insurance.config;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


@Configuration
@EnableCaching
@Slf4j
@RequiredArgsConstructor
public class CacheConfig {
    private final RedissonClient client;

    @Bean
    @Primary
    public CacheManager cacheManager() throws Exception {
        return getRedissonSpringCacheManager();
    }

    @PreDestroy
    private void shutdownRedissonClient() {
        if (client != null) {
            log.info("Shutting down cache manager redisson client");
            client.shutdown();
        }
    }

    private RedissonSpringCacheManager getRedissonSpringCacheManager() throws Exception {
        var cacheManager = new RedissonSpringCacheManager(client);
        cacheManager.setCacheNames(getCacheNames());

        return cacheManager;
    }

    private List<String> getCacheNames() throws Exception {
        var cacheNames = new ArrayList<String>();
        for (var field : CacheNames.class.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) {
                continue;
            }

            var obj = field.get(field.getName());
            if (obj instanceof String str) {
                cacheNames.add(str);
            }
        }

        return cacheNames;
    }
}
