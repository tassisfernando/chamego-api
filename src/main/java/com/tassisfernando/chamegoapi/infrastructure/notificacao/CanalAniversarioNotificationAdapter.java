package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.BirthdayNotificationPort;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@ConditionalOnProperty(prefix = "chamego.notificacao", name = "habilitada", havingValue = "true")
public class CanalAniversarioNotificationAdapter implements BirthdayNotificationPort {

    private static final Logger LOG = LoggerFactory.getLogger(CanalAniversarioNotificationAdapter.class);

    private final ChamegoNotificacaoProperties properties;
    private final RestClient restClient;

    public CanalAniversarioNotificationAdapter(ChamegoNotificacaoProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
    }

    @Override
    public void enviarResumoDiario(String assunto, String corpoTexto) {
        if (!properties.habilitada()) {
            return;
        }
        switch (properties.canal()) {
            case EMAIL -> {
                LOG.warn(
                        "Canal EMAIL selecionado, mas envio por SMTP ainda não está ativo na API — registrando no log.");
                LOG.info("{}\n{}", assunto, corpoTexto);
            }
            case WHATSAPP -> enviarWhatsApp(corpoTexto);
            default -> LOG.info("Aniversariantes (modo LOG):\n{}\n{}", assunto, corpoTexto);
        }
    }

    private void enviarWhatsApp(String corpoTexto) {
        String destino = properties.ownerWhatsapp();
        String url = properties.whatsappApiUrl();
        if (destino == null || destino.isBlank() || url == null || url.isBlank()) {
            LOG.warn("Canal WHATSAPP sem WHATSAPP_API_URL ou OWNER_WHATSAPP_NUMBER — caindo para LOG.");
            LOG.info("{}", corpoTexto);
            return;
        }
        try {
            restClient
                    .post()
                    .uri(url)
                    .body(java.util.Map.of("number", destino, "text", corpoTexto))
                    .retrieve()
                    .toBodilessEntity();
            LOG.info("Resumo de aniversariantes encaminhado via integração WhatsApp.");
        } catch (Exception ex) {
            LOG.error("Falha na integração WhatsApp (stub/API): {}", ex.getMessage());
            LOG.info("Conteúdo:\n{}", corpoTexto);
        }
    }
}