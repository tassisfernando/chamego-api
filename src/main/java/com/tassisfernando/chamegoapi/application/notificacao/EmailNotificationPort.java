package com.tassisfernando.chamegoapi.application.notificacao;

/** Porta de saída para envio do resumo diário por e-mail. */
public interface EmailNotificationPort {

    /**
     * @return {@code true} se o SMTP aceitou o envio; {@code false} se caiu no log (credenciais ausentes ou falha)
     */
    boolean enviarResumo(String destino, String assunto, String textoPlano, String corpoHtml);
}