package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.WhatsAppNotificationPort;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties.ProvedorWhatsApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Cliente HTTP compatível com Evolution API, Z-API e WhatsApp Cloud API.
 * Sem URL configurada, registra a mensagem no log e segue sem falhar o job.
 */
@Component
public class WhatsAppNotificationAdapter implements WhatsAppNotificationPort {

    private static final Logger LOG = LoggerFactory.getLogger(WhatsAppNotificationAdapter.class);

    private final ChamegoNotificacaoProperties properties;
    private final RestClient restClient;

    @Autowired
    public WhatsAppNotificationAdapter(ChamegoNotificacaoProperties properties) {
        this(properties, RestClient.builder().build());
    }

    WhatsAppNotificationAdapter(ChamegoNotificacaoProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    @Override
    public boolean enviarTexto(String numeroDestino, String mensagem) {
        String destino = apenasDigitos(numeroDestino);
        String url = properties.whatsappApiUrl();
        if (url == null || url.isBlank()) {
            LOG.warn(
                    "WhatsApp sem WHATSAPP_API_URL. Resumo para {} ficou só no log.",
                    mascarar(destino));
            LOG.info("Mensagem:\n{}", mensagem);
            return false;
        }
        if (destino.isBlank()) {
            LOG.error("WhatsApp da proprietária vazio. Mensagem não enviada.");
            return false;
        }
        String endpoint = montarEndpoint(url, properties.whatsappInstance(), properties.whatsappProvider());
        try {
            RestClient.RequestBodySpec requisicao = restClient
                    .post()
                    .uri(endpoint)
                    .contentType(MediaType.APPLICATION_JSON);
            String token = properties.whatsappApiToken();
            if (token != null && !token.isBlank()) {
                aplicarAutenticacao(requisicao, token, properties.whatsappProvider());
            } else {
                LOG.warn("WHATSAPP_API_TOKEN vazio. A chamada segue sem credencial.");
            }
            requisicao.body(corpo(destino, mensagem, properties.whatsappProvider())).retrieve().toBodilessEntity();
            LOG.info("Resumo diário encaminhado via WhatsApp para {}.", mascarar(destino));
            return true;
        } catch (RuntimeException ex) {
            LOG.error("Falha ao enviar WhatsApp para {}: {}", mascarar(destino), ex.getMessage());
            LOG.info("Conteúdo não entregue:\n{}", mensagem);
            return false;
        }
    }

    static String montarEndpoint(String baseUrl, String instance, ProvedorWhatsApp provedor) {
        String base = baseUrl.trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (provedor != ProvedorWhatsApp.EVOLUTION || instance == null || instance.isBlank()) {
            return base;
        }
        if (base.contains("/message/sendText/")) {
            return base;
        }
        return base + "/message/sendText/" + instance.trim();
    }

    private static void aplicarAutenticacao(
            RestClient.RequestBodySpec requisicao, String token, ProvedorWhatsApp provedor) {
        switch (provedor) {
            case ZAPI -> requisicao.header("Client-Token", token);
            case CLOUD -> requisicao.header("Authorization", "Bearer " + token);
            case EVOLUTION -> requisicao.header("apikey", token);
        }
    }

    private static Map<String, Object> corpo(String destino, String mensagem, ProvedorWhatsApp provedor) {
        return switch (provedor) {
            case ZAPI -> Map.of("phone", destino, "message", mensagem);
            case CLOUD -> {
                Map<String, Object> texto = new LinkedHashMap<>();
                texto.put("body", mensagem);
                Map<String, Object> corpo = new LinkedHashMap<>();
                corpo.put("messaging_product", "whatsapp");
                corpo.put("to", destino);
                corpo.put("type", "text");
                corpo.put("text", texto);
                yield corpo;
            }
            case EVOLUTION -> Map.of("number", destino, "text", mensagem);
        };
    }

    private static String apenasDigitos(String numero) {
        if (numero == null) {
            return "";
        }
        return numero.replaceAll("\\D", "");
    }

    private static String mascarar(String digitos) {
        if (digitos == null || digitos.length() <= 4) {
            return "****";
        }
        return digitos.substring(0, 2) + "****" + digitos.substring(digitos.length() - 2);
    }
}
