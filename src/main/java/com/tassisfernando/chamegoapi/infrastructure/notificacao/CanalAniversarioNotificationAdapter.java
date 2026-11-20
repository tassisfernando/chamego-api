package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.BirthdayNotificationPort;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CanalAniversarioNotificationAdapter implements BirthdayNotificationPort {

    private static final Logger LOG = LoggerFactory.getLogger(CanalAniversarioNotificationAdapter.class);

    private final ChamegoNotificacaoProperties properties;
    private final RestClient restClient;
    private final JavaMailSender mailSender;

    public CanalAniversarioNotificationAdapter(
            ChamegoNotificacaoProperties properties,
            org.springframework.beans.factory.ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.properties = properties;
        this.restClient = RestClient.builder().build();
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    @Override
    public void enviarResumoDiario(String assunto, String corpoTexto) {
        switch (properties.canal()) {
            case EMAIL -> enviarEmail(assunto, corpoTexto);
            case WHATSAPP -> enviarWhatsApp(corpoTexto);
            default -> LOG.info("Aniversariantes (modo LOG):\n{}\n{}", assunto, corpoTexto);
        }
    }

    private void enviarEmail(String assunto, String corpoTexto) {
        if (mailSender == null || properties.ownerEmail() == null || properties.ownerEmail().isBlank()) {
            LOG.warn(
                    "Canal EMAIL configurado, mas SMTP ou OWNER_NOTIFICATION_EMAIL ausente — caindo para LOG.");
            LOG.info("{}\n{}", assunto, corpoTexto);
            return;
        }
        var mensagem = new SimpleMailMessage();
        mensagem.setTo(properties.ownerEmail());
        mensagem.setSubject(assunto);
        mensagem.setText(corpoTexto);
        try {
            mailSender.send(mensagem);
        } catch (MailException ex) {
            LOG.error("Falha ao enviar e-mail de aniversariantes: {}", ex.getMessage());
            throw ex;
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