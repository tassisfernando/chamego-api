package com.tassisfernando.chamegoapi.infrastructure.persistence.pedido;

import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryAdapterTest {

    @Mock
    private PedidoJpaRepository jpaRepository;

    @Test
    void persisteEReidrataDataEntrega() {
        LocalDate entrega = LocalDate.of(2026, 12, 3);
        Pedido pedido = Pedido.reconstituir(
                UUID.randomUUID(),
                UUID.randomUUID(),
                LocalDate.of(2026, 11, 24),
                StatusPedido.ORCAMENTO,
                NaturezaPedido.VENDA,
                "combinado à tarde",
                null,
                entrega,
                List.of(new ItemPedido(UUID.randomUUID(), 1, brl("32.90"), brl("16.45"))));
        when(jpaRepository.findById(pedido.id())).thenReturn(Optional.empty());
        when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido salvo = new PedidoRepositoryAdapter(jpaRepository).salvar(pedido);

        ArgumentCaptor<PedidoJpaEntity> entidade = ArgumentCaptor.forClass(PedidoJpaEntity.class);
        verify(jpaRepository).save(entidade.capture());
        assertEquals(entrega, entidade.getValue().getDataEntrega());
        assertEquals(entrega, salvo.dataEntrega());
        assertEquals(pedido.id(), salvo.id());
    }

    @Test
    void mantemDataEntregaNulaQuandoNaoHouveAgenda() {
        Pedido pedido = Pedido.novo(UUID.randomUUID(), LocalDate.of(2026, 11, 24), NaturezaPedido.VENDA, null);
        pedido.adicionarItem(new ItemPedido(UUID.randomUUID(), 1, brl("10.00"), brl("4.00")));
        pedido.registrarComoOrcamento();
        when(jpaRepository.findById(pedido.id())).thenReturn(Optional.empty());
        when(jpaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Pedido salvo = new PedidoRepositoryAdapter(jpaRepository).salvar(pedido);

        assertNull(salvo.dataEntrega());
    }
}
