package ro.vitoc.licenta.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ro.vitoc.licenta.core.config.RedisUtil;
import ro.vitoc.licenta.core.model.WebMicroService;

@Configuration
public class WebMicroServiceCacheManagerImpl implements CacheManager<WebMicroService> {
    public static final String TABLE_SIMPLEPROJECT = "TABLE_MICROSERVICE";
    public static final String SIMPLEPROJECT = "MICROSERVICE_";
    private RedisUtil<WebMicroService> redisUtil;

    @Autowired
    public WebMicroServiceCacheManagerImpl(RedisUtil<WebMicroService> redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public void cacheValue(WebMicroService project) {
        redisUtil.save(project,SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }

    @Override
    public WebMicroService checkCache(WebMicroService project) {
        return redisUtil.find(SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }
}