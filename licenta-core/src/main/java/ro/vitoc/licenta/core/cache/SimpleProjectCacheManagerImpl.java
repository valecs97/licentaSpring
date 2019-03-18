package ro.vitoc.licenta.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ro.vitoc.licenta.core.config.RedisUtil;
import ro.vitoc.licenta.core.model.SimpleProject;

@Configuration
public class SimpleProjectCacheManagerImpl implements CacheManager<SimpleProject> {
    public static final String TABLE_SIMPLEPROJECT = "TABLE_SIMPLEPROJECT";
    public static final String SIMPLEPROJECT = "SIMPLEPROJECT_";
    private RedisUtil<SimpleProject> redisUtil;

    @Autowired
    public SimpleProjectCacheManagerImpl(RedisUtil<SimpleProject> redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Override
    public void cacheValue(SimpleProject project) {
        //redisUtil.putMap(TABLE_SIMPLEPROJECT,SIMPLEPROJECT+project.getName(),project);
        //redisUtil.setExpire(TABLE_SIMPLEPROJECT,1, TimeUnit.DAYS);
        redisUtil.save(project,SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }

    @Override
    public SimpleProject checkCache(SimpleProject project) {
        //System.out.println(redisUtil.getMapAsAll(TABLE_SIMPLEPROJECT).size());
        //return redisUtil.getMapAsSingleEntry(TABLE_SIMPLEPROJECT,SIMPLEPROJECT+project.getName());
        return redisUtil.find(SIMPLEPROJECT + project.getName(),TABLE_SIMPLEPROJECT);
    }
}
