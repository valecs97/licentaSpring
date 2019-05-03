package ro.vitoc.licenta.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import ro.vitoc.licenta.core.config.RedisUtil;
import ro.vitoc.licenta.core.model.Log;

import java.util.List;

@Configuration
public class LogCacheManager implements CacheManager<Log> {
    public static final String TABLE_LOG = "TABLE_LOG";
    public static final String LOG = "LOG_";
    @Autowired
    private RedisUtil<Log> redisUtil;

    @Override
    public void cacheValue(Log T) {
        redisUtil.save(T,LOG + T.getId(),TABLE_LOG);
    }

    @Override
    public Log checkCache(Log T) {
        return redisUtil.find(LOG + T.getId(),TABLE_LOG);
    }

    public List<Log> getCache(){
        return redisUtil.findALL(TABLE_LOG);
    }
}
