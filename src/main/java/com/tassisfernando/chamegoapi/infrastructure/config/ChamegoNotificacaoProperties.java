package com.tassisfernando.chamegoapi.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chamego.notificacao")
public record ChamegoNotificacaoProperties(
        Canal canal, String ownerEmail, String ownerWhatsapp, String whatsappApiUrl) {

    public enum Canal {
        LOG,
        EMAIL,
        WHATSAPP
    }

    public ChamegoNotificacaoProperties {
        if (canal == null) {
            canal = Canal.LOG;
        }
    }
}