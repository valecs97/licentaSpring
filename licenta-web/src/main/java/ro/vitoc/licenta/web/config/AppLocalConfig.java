package ro.vitoc.licenta.web.config;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import ro.vitoc.licenta.core.config.JPAConfig;

@Configuration
@ComponentScan({"ro.vitoc.licenta.core", "ro.vitoc.licenta.miscellaneous"})
@Import({JPAConfig.class})
@PropertySources({@PropertySource(value = "classpath:local/db.properties"),@PropertySource(value = "classpath:local/redis.properties"),@PropertySource(value = "classpath:local/docker.properties")
})
public class AppLocalConfig {
    /**
     * Enables placeholders usage with SpEL expressions.
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
