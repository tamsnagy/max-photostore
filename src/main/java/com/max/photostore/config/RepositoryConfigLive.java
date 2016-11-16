package com.max.photostore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableJpaRepositories(basePackages = "com.max.photostore.repository")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.max.photostore.domain")
@Profile("live")
public class RepositoryConfigLive extends RepositoryConfig{
    private static final String dbUriEnv = System.getenv("DATABASE_URL");
    private static final String redisUriEnv = System.getenv("REDIS_URL");

    public RepositoryConfigLive() throws URISyntaxException {
    }


    @Bean
    public DataSource dataSource()  {
        return aDataSource();
    }

    @Bean
    public JedisConnectionFactory jedisConnFactory() {
        return aJedisConnFactory();
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Override
    protected String dbUsername() throws URISyntaxException {
        final URI dbUri = new URI(dbUriEnv);
        return dbUri.getUserInfo().split(":")[0];
    }

    @Override
    protected String dbUrl() throws URISyntaxException {
        final URI dbUri = new URI(dbUriEnv);
        return "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
    }

    @Override
    protected String dbPassword() throws URISyntaxException {
        final URI dbUri = new URI(dbUriEnv);
        return dbUri.getUserInfo().split(":")[1];
    }


    @Override
    protected int redisPort() throws URISyntaxException {
        final URI redisUri = new URI(redisUriEnv);
        return redisUri.getPort();
    }

    @Override
    protected String redisHost() throws URISyntaxException {
        final URI redisUri = new URI(redisUriEnv);
        return redisUri.getHost();
    }

    @Override
    protected String redisPassword() throws URISyntaxException {
        final URI redisUri = new URI(redisUriEnv);
        return redisUri.getUserInfo().split(":", 2)[1];
    }
}
