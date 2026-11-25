package com.tassisfernando.chamegoapi.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "chamego.notificacao")
public record ChamegoNotificacaoProperties(
        boolean habilitada,
        Canal canal,
        String ownerEmail,
        String ownerWhatsapp,
        String ownerName,
        String whatsappApiUrl,
        String whatsappApiToken,
        String whatsappInstance,
        ProvedorWhatsApp whatsappProvider) {

    public enum Canal {
        LOG,
        EMAIL,
        WHATSAPP
    }

    public enum ProvedorWhatsApp {
        EVOLUTION,
        ZAPI,
        CLOUD
    }

    public ChamegoNotificacaoProperties {
        if (canal == null) {
            canal = Canal.LOG;
        }
        if (ownerWhatsapp == null || ownerWhatsapp.isBlank()) {
            ownerWhatsapp = "+5531989319621";
        }
        if (ownerName == null || ownerName.isBlank()) {
            ownerName = "Maressa";
        }
        if (whatsappApiUrl == null) {
            whatsappApiUrl = "";
        }
        if (whatsappApiToken == null) {
            whatsappApiToken = "";
        }
        if (whatsappInstance == null) {
            whatsappInstance = "";
        }
        if (whatsappProvider == null) {
            whatsappProvider = ProvedorWhatsApp.EVOLUTION;
        }
    }

    public boolean envioExternoAtivo() {
        return habilitada && (canal == Canal.EMAIL || canal == Canal.WHATSAPP);
    }
}