package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.cliente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotifyDailyBirthdaysUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private BirthdayNotificationPort birthdayNotificationPort;

    private NotifyDailyBirthdaysUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new NotifyDailyBirthdaysUseCase(clienteRepository, birthdayNotificationPort);
    }

    @Test
    void naoEnviaQuandoListaVazia() {
        LocalDate hoje = LocalDate.of(2026, 7, 3);
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of());

        int quantidade = useCase.executar(hoje);

        assertEquals(0, quantidade);
        verify(birthdayNotificationPort, never()).enviarResumoDiario(any(), any());
    }

    @Test
    void enviaResumoQuandoHaAniversariantesAtivos() {
        LocalDate hoje = LocalDate.of(2026, 7, 3);
        Cliente maria = cliente("Maria");
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of(maria));

        int quantidade = useCase.executar(hoje);

        assertEquals(1, quantidade);
        verify(birthdayNotificationPort).enviarResumoDiario(any(), any());
    }

    @Test
    void ignoraClientesInativos() {
        LocalDate hoje = LocalDate.of(2026, 7, 3);
        Cliente inativo = cliente("Inativo");
        inativo.desativar();
        when(clienteRepository.buscarAniversariantesDoDia(MonthDay.from(hoje))).thenReturn(List.of(inativo));

        int quantidade = useCase.executar(hoje);

        assertEquals(0, quantidade);
        verify(birthdayNotificationPort, never()).enviarResumoDiario(any(), any());
    }
}