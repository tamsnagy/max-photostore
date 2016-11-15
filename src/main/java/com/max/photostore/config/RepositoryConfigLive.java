package com.max.photostore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

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
//        final URI dbUri = new URI(System.getenv("DATABASE_URL"));
//
//        final String username = dbUri.getUserInfo().split(":")[0];
//        final String password = dbUri.getUserInfo().split(":")[1];
//        final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
//
//        return DataSourceBuilder
//                .create()
//                .username(username)
//                .password(password)
//                .url(dbUrl)
//                .driverClassName("org.postgresql.Driver")
//                .build();
        return aDataSource();
    }

    @Bean
    public JedisConnectionFactory jedisConnFactory() {

//        try {
//            String redistogoUrl = System.getenv("REDIS_URL");
//            URI redistogoUri = new URI(redistogoUrl);
//
//            JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();
//
//            jedisConnFactory.setUsePool(true);
//            jedisConnFactory.setHostName(redistogoUri.getHost());
//            jedisConnFactory.setPort(redistogoUri.getPort());
//            jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
//            jedisConnFactory.setPassword(redistogoUri.getUserInfo().split(":", 2)[1]);
//
//            return jedisConnFactory;
//
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            return null;
//        }
        return aJedisConnFactory();
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
