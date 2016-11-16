package com.max.photostore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import javax.sql.DataSource;
import java.net.URISyntaxException;

@Configuration
@EnableJpaRepositories(basePackages = "com.max.photostore.repository")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.max.photostore.domain")
@Profile("dev")
public class RepositoryConfigDev extends RepositoryConfig{

    public RepositoryConfigDev() throws URISyntaxException {
    }

    @Bean
    public DataSource dataSource()  {
        return aDataSource();
    }

    @Bean
    public JedisConnectionFactory jedisConnFactory() {
        return aJedisConnFactory();
    }

    @Override
    protected String dbUsername() throws URISyntaxException {
        return "postgres";
    }

    @Override
    protected String dbUrl() throws URISyntaxException {
        return "jdbc:postgresql://localhost:5432/postgres";
    }

    @Override
    protected String dbPassword() throws URISyntaxException {
        return "postgres";
    }

    @Override
    protected int redisPort() throws URISyntaxException {
        return 6379;
    }

    @Override
    protected String redisHost() throws URISyntaxException {
        return "localhost";
    }

    @Override
    protected String redisPassword() throws URISyntaxException {
        return null;
    }
}
