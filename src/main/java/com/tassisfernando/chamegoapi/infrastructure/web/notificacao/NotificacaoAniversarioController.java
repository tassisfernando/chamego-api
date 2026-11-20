package com.tassisfernando.chamegoapi.infrastructure.web.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.NotifyDailyBirthdaysUseCase;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications/birthdays")
public class NotificacaoAniversarioController {

    private final ObjectProvider<NotifyDailyBirthdaysUseCase> notifyDailyBirthdaysUseCase;
    private final ChamegoNotificacaoProperties notificacaoProperties;

    public NotificacaoAniversarioController(
            ObjectProvider<NotifyDailyBirthdaysUseCase> notifyDailyBirthdaysUseCase,
            ChamegoNotificacaoProperties notificacaoProperties) {
        this.notifyDailyBirthdaysUseCase = notifyDailyBirthdaysUseCase;
        this.notificacaoProperties = notificacaoProperties;
    }

    @GetMapping("/config")
    public Map<String, Object> configuracao() {
        return Map.of(
                "habilitada",
                notificacaoProperties.habilitada(),
                "canal",
                notificacaoProperties.canal().name(),
                "ownerEmail",
                mascarar(notificacaoProperties.ownerEmail()),
                "ownerWhatsapp",
                mascarar(notificacaoProperties.ownerWhatsapp()),
                "whatsappApiUrl",
                notificacaoProperties.whatsappApiUrl() != null
                        ? notificacaoProperties.whatsappApiUrl()
                        : "");
    }

    @PostMapping("/trigger")
    public Map<String, Object> disparar() {
        if (!notificacaoProperties.habilitada()) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Notificações de aniversário desabilitadas (NOTIFICACOES_HABILITADAS=false).");
        }
        NotifyDailyBirthdaysUseCase useCase =
                notifyDailyBirthdaysUseCase.getIfAvailable();
        if (useCase == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Serviço de notificação indisponível.");
        }
        LocalDate hoje = LocalDate.now(NotifyDailyBirthdaysUseCase.FUSO_OPERACAO);
        int quantidade = useCase.executar(hoje);
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