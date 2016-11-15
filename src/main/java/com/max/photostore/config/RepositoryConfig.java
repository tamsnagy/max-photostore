package com.max.photostore.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.max.photostore.repository")
@EnableAutoConfiguration
@EntityScan(basePackages = "com.max.photostore.domain")
public class RepositoryConfig {
}
