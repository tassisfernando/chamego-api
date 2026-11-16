package com.tassisfernando.chamegoapi.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Infra de agendamento fora do lazy-init: {@code @Scheduled} precisa do
 * {@link org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor}
 * ativo na subida, mesmo com {@code spring.main.lazy-initialization=true}.
 */
@Configuration
@EnableScheduling
@Lazy(false)
public class AgendamentoConfig {
}