package ro.vitoc.licenta.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ro.vitoc.licenta.core.config.RedisUtil;
import ro.vitoc.licenta.core.model.MicroService;

@Configuration
public class MicroServiceCacheManagerImpl implements CacheManager<MicroService> {
    public static final String TABLE_SIMPLEPROJECT = "TABLE_MICROSERVICE";
    public static final String SIMPLEPROJECT = "MICROSERVICE_";
    private RedisUtil<MicroService> redisUtil;

    @Autowired
    public MicroServiceCacheManagerImpl(RedisUtil<MicroService> redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public void cacheValue(MicroService project) {
        redisUtil.save(project,SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }

    @Override
    public MicroService checkCache(MicroService project) {
        return redisUtil.find(SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }
}
