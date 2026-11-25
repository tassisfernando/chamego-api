package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.SendDailyOwnerDigestUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class ResumoDiarioProprietariaJob {

    private static final Logger LOG = LoggerFactory.getLogger(ResumoDiarioProprietariaJob.class);

    private final SendDailyOwnerDigestUseCase sendDailyOwnerDigestUseCase;

    public ResumoDiarioProprietariaJob(SendDailyOwnerDigestUseCase sendDailyOwnerDigestUseCase) {
        this.sendDailyOwnerDigestUseCase = sendDailyOwnerDigestUseCase;
    }

    @Scheduled(cron = "0 0 7 * * *", zone = "America/Sao_Paulo")
    public void enviarResumoDasSete() {
        try {
            var resultado = sendDailyOwnerDigestUseCase.executar();
            if (!resultado.notificacaoEnviada()) {
                LOG.info(
                        "Resumo das 7h omitido em {}: sem aniversário nem entrega.",
                        resultado.data());
                return;
            }
            LOG.info(
                    "Resumo das 7h preparado ({} aniversariante(s), {} entrega(s), api={}).",
                    resultado.aniversariantes(),
                    resultado.entregas(),
                    resultado.entreguePelaApi());
        } catch (RuntimeException ex) {
            LOG.error("Falha no resumo diário das 7h: {}", ex.getMessage(), ex);
        }
    }
}
