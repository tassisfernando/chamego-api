package com.tassisfernando.chamegoapi.infrastructure.web.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.NotifyDailyBirthdaysUseCase;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/birthdays")
public class NotificacaoAniversarioController {

    private final NotifyDailyBirthdaysUseCase notifyDailyBirthdaysUseCase;
    private final ChamegoNotificacaoProperties notificacaoProperties;

    public NotificacaoAniversarioController(
            NotifyDailyBirthdaysUseCase notifyDailyBirthdaysUseCase,
            ChamegoNotificacaoProperties notificacaoProperties) {
        this.notifyDailyBirthdaysUseCase = notifyDailyBirthdaysUseCase;
        this.notificacaoProperties = notificacaoProperties;
    }

    @GetMapping("/config")
    public Map<String, Object> configuracao() {
        return Map.of(
                "canal", notificacaoProperties.canal().name(),
                "ownerEmail", mascarar(notificacaoProperties.ownerEmail()),
                "ownerWhatsapp", mascarar(notificacaoProperties.ownerWhatsapp()),
                "whatsappApiUrl", notificacaoProperties.whatsappApiUrl() != null
                        ? notificacaoProperties.whatsappApiUrl()
                        : "");
    }

    @PostMapping("/trigger")
    public Map<String, Object> disparar() {
        LocalDate hoje = LocalDate.now(NotifyDailyBirthdaysUseCase.FUSO_OPERACAO);
        int quantidade = notifyDailyBirthdaysUseCase.executar(hoje);
        return Map.of(
                "dataReferencia",
                hoje.toString(),
                "aniversariantes",
                quantidade,
                "notificacaoEnviada",
                quantidade > 0);
    }

    private static String mascarar(String valor) {
        if (valor == null || valor.isBlank()) {
            return "";
        }
        if (valor.length() <= 4) {
            return "****";
        }
        return valor.substring(0, 2) + "****" + valor.substring(valor.length() - 2);
    }
}