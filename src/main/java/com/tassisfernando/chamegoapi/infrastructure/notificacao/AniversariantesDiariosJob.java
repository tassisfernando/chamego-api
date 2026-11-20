package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.NotifyDailyBirthdaysUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AniversariantesDiariosJob {

    private static final Logger LOG = LoggerFactory.getLogger(AniversariantesDiariosJob.class);

    private final NotifyDailyBirthdaysUseCase notifyDailyBirthdaysUseCase;

    public AniversariantesDiariosJob(NotifyDailyBirthdaysUseCase notifyDailyBirthdaysUseCase) {
        this.notifyDailyBirthdaysUseCase = notifyDailyBirthdaysUseCase;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Sao_Paulo")
    public void notificarProprietaria() {
        int quantidade = notifyDailyBirthdaysUseCase.executar();
        if (quantidade > 0) {
            LOG.info("Notificação de aniversariantes enviada para {} cliente(s).", quantidade);
        }
    }
}