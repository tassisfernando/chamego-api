package com.tassisfernando.chamegoapi;

import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoDigestNotificacaoProperties;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ChamegoNotificacaoProperties.class, ChamegoDigestNotificacaoProperties.class})
public class ChamegoApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChamegoApiApplication.class, args);
    }
}
