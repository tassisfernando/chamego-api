package com.tassisfernando.chamegoapi.application.notificacao;

public interface BirthdayNotificationPort {

    void enviarResumoDiario(String assunto, String corpoTexto);
}