package com.tassisfernando.chamegoapi.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chamego.notificacao.digest")
public record ChamegoDigestNotificacaoProperties(boolean emailEnabled, boolean whatsappEnabled, String ownerEmail) {

    public ChamegoDigestNotificacaoProperties {
        if (ownerEmail == null || ownerEmail.isBlank()) {
            ownerEmail = "tsfernando72@gmail.com";
        }
    }

    public String emailProprietaria() {
        return ownerEmail.trim();
    }
}