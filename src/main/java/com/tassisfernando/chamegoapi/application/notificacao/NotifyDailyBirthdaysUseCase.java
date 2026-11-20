package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.ZoneId;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "chamego.notificacao", name = "habilitada", havingValue = "true")
public class NotifyDailyBirthdaysUseCase {

    public static final ZoneId FUSO_OPERACAO = ZoneId.of("America/Sao_Paulo");

    private final ClienteRepository clienteRepository;
    private final BirthdayNotificationPort birthdayNotificationPort;

    public NotifyDailyBirthdaysUseCase(
            ClienteRepository clienteRepository, BirthdayNotificationPort birthdayNotificationPort) {
        this.clienteRepository = clienteRepository;
        this.birthdayNotificationPort = birthdayNotificationPort;
    }

    @Transactional(readOnly = true)
    public int executar() {
        return executar(LocalDate.now(FUSO_OPERACAO));
    }

    @Transactional(readOnly = true)
    public int executar(LocalDate referencia) {
        MonthDay dia = MonthDay.from(referencia);
        List<Cliente> aniversariantes = clienteRepository.buscarAniversariantesDoDia(dia).stream()
                .filter(Cliente::ativo)
                .toList();
        var mensagem = MensagemAniversariantesDiarios.montar(aniversariantes, referencia);
        if (mensagem.isEmpty()) {
            return 0;
        }
        birthdayNotificationPort.enviarResumoDiario(
                MensagemAniversariantesDiarios.assunto(referencia), mensagem.get());
        return aniversariantes.size();
    }
}