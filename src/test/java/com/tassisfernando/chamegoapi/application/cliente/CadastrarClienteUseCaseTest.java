package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CadastrarClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private CadastrarClienteUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CadastrarClienteUseCase(clienteRepository);
        when(clienteRepository.salvar(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveCadastrarClienteComDadosDaPlanilha() {
        Cliente cliente = useCase.executar(new CadastrarClienteComando(
                "Tácila Tayane", "31987068258", "IPATINGA", "MG", LocalDate.of(1990, 7, 3)));

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).salvar(captor.capture());
        assertEquals("Tácila Tayane", captor.getValue().nome());
        assertEquals("Tácila Tayane", cliente.nome());
        assertEquals("31987068258", cliente.telefone());
    }
}
