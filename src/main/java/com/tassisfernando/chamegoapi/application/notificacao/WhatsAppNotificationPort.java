package com.tassisfernando.chamegoapi.application.notificacao;

/** Porta de saída para mensagem de texto no WhatsApp da proprietária. */
public interface WhatsAppNotificationPort {

    /**
     * @return {@code true} quando a API aceitou o envio; {@code false} quando caiu no log
     *     (credenciais ausentes ou falha HTTP já registrada)
     */
    boolean enviarTexto(String numeroDestino, String mensagem);
}
