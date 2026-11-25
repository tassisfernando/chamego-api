package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.EmailNotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailDailyDigestAdapter implements EmailNotificationPort {

    private static final Logger LOG = LoggerFactory.getLogger(EmailDailyDigestAdapter.class);

    private final JavaMailSender mailSender;
    private final String usuarioSmtp;

    public EmailDailyDigestAdapter(
            @Autowired(required = false) JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String usuarioSmtp) {
        this.mailSender = mailSender;
        this.usuarioSmtp = usuarioSmtp == null ? "" : usuarioSmtp.trim();
    }

    @Override
    public boolean enviarResumo(String destino, String assunto, String textoPlano, String corpoHtml) {
        if (destino == null || destino.isBlank()) {
            LOG.warn("Resumo diário sem OWNER_NOTIFICATION_EMAIL — registrando no log.");
            LOG.info("{}\n{}", assunto, textoPlano);
            return false;
        }
        if (mailSender == null || usuarioSmtp.isBlank()) {
            LOG.warn(
                    "SMTP não configurado (SPRING_MAIL_USERNAME vazio). Resumo para {} ficou no log.",
                    mascararEmail(destino));
            LOG.info("Assunto: {}\n\n{}\n\n--- HTML ---\n{}", assunto, textoPlano, corpoHtml);
            return false;
        }
        try {
            MimeMessage mensagem = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensagem, true, "UTF-8");
            helper.setFrom(usuarioSmtp);
            helper.setTo(destino.trim());
            helper.setSubject(assunto);
            helper.setText(textoPlano, corpoHtml);
            mailSender.send(mensagem);
            LOG.info("Resumo diário enviado por e-mail para {}.", mascararEmail(destino));
            return true;
        } catch (MessagingException | MailException ex) {
            LOG.error("Falha ao enviar resumo por e-mail para {}: {}", mascararEmail(destino), ex.getMessage());
            LOG.info("Conteúdo não entregue:\n{}", textoPlano);
            return false;
        }
    }

    private static String mascararEmail(String email) {
        int arroba = email.indexOf('@');
        if (arroba <= 1) {
            return "****";
        }
        return email.substring(0, 2) + "****" + email.substring(arroba);
    }
}