package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailDailyDigestAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void registraNoLogQuandoUsuarioSmtpVazio() {
        var adapter = new EmailDailyDigestAdapter(null, "");

        assertFalse(adapter.enviarResumo("tsfernando72@gmail.com", "Assunto", "Corpo", "<p>HTML</p>"));
    }

    @Test
    void enviaQuandoSmtpConfigurado() throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
        var adapter = new EmailDailyDigestAdapter(mailSender, "conta@gmail.com");

        assertTrue(adapter.enviarResumo("tsfernando72@gmail.com", "Assunto", "Corpo", "<p>HTML</p>"));

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void naoPropagaFalhaDeSmtp() throws Exception {
        Session session = Session.getDefaultInstance(new Properties());
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
        doThrow(new org.springframework.mail.MailSendException("falha")).when(mailSender).send(any(MimeMessage.class));
        var adapter = new EmailDailyDigestAdapter(mailSender, "conta@gmail.com");

        assertFalse(adapter.enviarResumo("destino@gmail.com", "Assunto", "Corpo", "<p>x</p>"));
    }
}