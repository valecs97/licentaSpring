package ro.vitoc.licenta.core.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import ro.vitoc.licenta.miscellaneous.preCheck.DockerPreCheckImpl;
import ro.vitoc.licenta.miscellaneous.preConfig.DockerPreConfig;
import ro.vitoc.licenta.miscellaneous.preConfig.DockerPreConfigImpl;
import ro.vitoc.licenta.miscellaneous.service.ProcessService;
import ro.vitoc.licenta.miscellaneous.service.ProcessServiceImpl;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${redis.hostname}")
    private String redisHostName;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${docker.defaultVMName}")
    private String defaultVMName;

    private class GetDefaultVMHostNameClass implements Runnable {

        @Override
        public void run() {
            log.trace("GetDefaultVMHostNameClass trace");
            if (redisHostName.equals("127.0.0.1") || redisHostName.equals("localhost")) {
                try {
                    DockerPreCheckImpl.VMStatus.await();
                    redisHostName = ProcessServiceImpl.getDefaultVMInfoGlobal(defaultVMName)
                            .split(" ")[2].split("//")[1].split(":")[0];

                } catch (InterruptedException e) {
                    log.trace("GetDefaultVMHostName thread intrerrupted");
                }
            }
        }
    }

    @PostConstruct
    private void GetDefaultVMHostName() {
        GetDefaultVMHostNameClass runnable = new GetDefaultVMHostNameClass();
        Thread thread = new Thread(runnable);
        thread.start();

    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager.RedisCacheManagerBuilder builder =
                RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory());
        return builder.build();
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        System.out.println(redisHostName + " " + redisPort);
        RedisStandaloneConfiguration configuration =
                new RedisStandaloneConfiguration(redisHostName, 6379);
        JedisConnectionFactory res = new JedisConnectionFactory(configuration);
        return res;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        //RedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //RedisSerializer stringRedisSerializer = new StringRedisSerializer();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        //redisTemplate.setKeySerializer(stringRedisSerializer);
        //redisTemplate.setHashKeySerializer(stringRedisSerializer);
        redisTemplate.setConnectionFactory(jedisConnectionFactory());

        return redisTemplate;
    }
}
