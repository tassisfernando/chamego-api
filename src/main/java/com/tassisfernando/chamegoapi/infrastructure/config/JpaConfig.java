package com.tassisfernando.chamegoapi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.tassisfernando.chamegoapi.infrastructure.persistence")
public class JpaConfig {
}
