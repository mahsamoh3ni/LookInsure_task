package insurance.aop.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheAspect {
    private final CacheManager cacheManager;

    @Pointcut("@annotation(insurance.annotations.CleanAllCaches)")
    public void cleanCacheResourcePointcut() {
        // Method is empty as this is just a Pointcut, the implementations are in the advices.
    }

    @Around("cleanCacheResourcePointcut()")
    public Object cleanCacheAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            cleanAllCaches();
            return joinPoint.proceed();

        } catch (Throwable ex) {
            throw ex;
        }
    }

    private void cleanAllCaches() {
        if (cacheManager instanceof NoOpCacheManager) {
            log.warn("cache type is noop, so nothing to do");
            return;
        }

        cacheManager.getCacheNames()
                .stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(Cache::clear);
    }
}
