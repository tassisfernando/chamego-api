package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PedidoTest {

    @Test
    void deveCalcularLucroBrutoDeVendaComSucesso() {
        Pedido venda = Pedido.novo(UUID.randomUUID(), LocalDate.now(), NaturezaPedido.VENDA, null);
        venda.adicionarItem(new ItemPedido(UUID.randomUUID(), 2, brl("32.90"), brl("16.45")));
        venda.confirmar();

        assertEquals(brl("65.80"), venda.receitaContabil());
        assertEquals(brl("32.90"), venda.custoTotal());
        assertEquals(brl("32.90"), venda.lucroBruto());
        assertTrue(venda.estaConfirmado());
    }

    @Test
    void deveZerarReceitaContabilEmDoacao() {
        Pedido doacao = Pedido.novo(UUID.randomUUID(), LocalDate.now(), NaturezaPedido.DOACAO, null);
        doacao.adicionarItem(new ItemPedido(UUID.randomUUID(), 1, brl("32.90"), brl("16.45")));
        doacao.confirmar();

        assertEquals(brl("0.00"), doacao.receitaContabil());
        assertEquals(brl("-16.45"), doacao.lucroBruto());
    }

    @Test
    void deveRecusarConfirmacaoSemItens() {
        Pedido pedido = Pedido.novo(UUID.randomUUID(), LocalDate.now(), NaturezaPedido.VENDA, null);

        assertThrows(RegraDeNegocioViolada.class, pedido::confirmar);
    }

    @Test
    void deveRecusarAlteracaoAposConfirmacao() {
        Pedido pedido = Pedido.novo(UUID.randomUUID(), LocalDate.now(), NaturezaPedido.VENDA, null);
        pedido.adicionarItem(new ItemPedido(UUID.randomUUID(), 1, brl("32.90"), brl("16.45")));
        pedido.confirmar();

        assertThrows(
                RegraDeNegocioViolada.class,
                () -> pedido.adicionarItem(new ItemPedido(UUID.randomUUID(), 1, brl("10.00"), brl("5.00"))));
    }
}
