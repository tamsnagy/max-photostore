package com.max.photostore.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.Protocol;

import javax.sql.DataSource;
import java.net.URISyntaxException;

public abstract class RepositoryConfig {


    final String dbUsername;
    final String dbPassword;
    final String dbUrl;
    final String redisPassword;
    final String redisHost;
    final int redisPort;

    public RepositoryConfig() throws URISyntaxException {
        this.dbUsername = dbUsername();
        this.dbPassword = dbPassword();
        this.dbUrl = dbUrl();
        this.redisPassword = redisPassword();
        this.redisHost = redisHost();
        this.redisPort = redisPort();
    }


    protected DataSource aDataSource() {
        return DataSourceBuilder
                .create()
                .username(dbUsername)
                .password(dbPassword)
                .url(dbUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }


    protected JedisConnectionFactory aJedisConnFactory() {
        JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory();

        jedisConnFactory.setUsePool(true);
        jedisConnFactory.setHostName(redisHost);
        jedisConnFactory.setPort(redisPort);
        jedisConnFactory.setTimeout(Protocol.DEFAULT_TIMEOUT);
        jedisConnFactory.setPassword(redisPassword);

        return jedisConnFactory;
    }

    protected abstract String dbUsername() throws URISyntaxException;

    protected abstract String dbUrl() throws URISyntaxException;

    protected abstract String dbPassword() throws URISyntaxException;

    protected abstract int redisPort() throws URISyntaxException;

    protected abstract String redisHost() throws URISyntaxException;

    protected abstract String redisPassword() throws URISyntaxException;

}
