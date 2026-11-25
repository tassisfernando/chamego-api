package com.tassisfernando.chamegoapi.infrastructure.web.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.SendDailyOwnerDigestUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping({"/api/notifications/daily-digest", "/notifications/daily-digest"})
public class ResumoDiarioController {

    private final SendDailyOwnerDigestUseCase sendDailyOwnerDigestUseCase;

    public ResumoDiarioController(SendDailyOwnerDigestUseCase sendDailyOwnerDigestUseCase) {
        this.sendDailyOwnerDigestUseCase = sendDailyOwnerDigestUseCase;
    }

    @PostMapping("/trigger")
    public Map<String, Object> disparar() {
        var resultado = sendDailyOwnerDigestUseCase.executar();
        return Map.of(
                "dataReferencia", resultado.data().toString(),
                "aniversariantes", resultado.aniversariantes(),
                "entregas", resultado.entregas(),
                "notificacaoEnviada", resultado.notificacaoEnviada(),
                "entreguePorEmail", resultado.entreguePorEmail(),
                "entreguePorWhatsapp", resultado.entreguePorWhatsapp(),
                "entreguePelaApi", resultado.entreguePelaApi(),
                "canal", resultado.canal(),
                "emailDestino", resultado.emailDestino());
    }
}
