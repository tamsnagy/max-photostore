package com.max.photostore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.URISyntaxException;


@Configuration
@EnableJpaRepositories(basePackages = "com.max.photostore.repository")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.max.photostore.domain")
@Profile("test")
public class RepositoryConfigTest extends RepositoryConfig {


    public RepositoryConfigTest() throws URISyntaxException {
    }

    @Override
    protected String dbUsername() throws URISyntaxException {
        return "username";
    }

    @Override
    protected String dbUrl() throws URISyntaxException {
        return "jdbc:h2:mem:test;MODE=PostgreSQL";
    }

    @Override
    protected String dbPassword() throws URISyntaxException {
        return "password";
    }

    @Override
    protected int redisPort() throws URISyntaxException {
        return 6378;
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
