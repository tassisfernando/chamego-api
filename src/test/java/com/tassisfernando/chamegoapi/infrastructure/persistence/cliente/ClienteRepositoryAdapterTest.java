package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.MonthDay;
import java.util.List;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.cliente;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteRepositoryAdapterTest {

    @Mock
    private ClienteJpaRepository jpaRepository;

    private ClienteRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ClienteRepositoryAdapter(jpaRepository, new ClientePersistenceMapper());
    }

    @Test
    void devePersistirClienteViaRepositorioJpa() {
        Cliente cliente = cliente("Quésia Paula");
        when(jpaRepository.save(any(ClienteJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cliente persistido = adapter.salvar(cliente);

        ArgumentCaptor<ClienteJpaEntity> captor = ArgumentCaptor.forClass(ClienteJpaEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals(cliente.nome(), captor.getValue().getNome());
        assertEquals(cliente.nome(), persistido.nome());
    }

    @Test
    void deveBuscarAniversariantesDelegandoMesEDia() {
        when(jpaRepository.buscarAniversariantes(7, 3)).thenReturn(List.of());

        assertTrue(adapter.buscarAniversariantesDoDia(MonthDay.of(7, 3)).isEmpty());
        verify(jpaRepository).buscarAniversariantes(7, 3);
    }

    @Test
    void deveRetornarVazioQuandoClienteNaoExistir() {
        when(jpaRepository.findById(any())).thenReturn(Optional.empty());

        assertTrue(adapter.buscarPorId(java.util.UUID.randomUUID()).isEmpty());
    }
}
