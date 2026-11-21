package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PrecificacaoCalculosTest {

    @Test
    void calculaPrecoAPartirDaMargem() {
        var custo = brl("20.00");
        var resultado = PrecificacaoCalculos.calcularPorMargem(custo, new BigDecimal("100"));
        assertEquals(0, new BigDecimal("40.00").compareTo(resultado.precoVenda().valor()));
        assertEquals(0, new BigDecimal("20.00").compareTo(resultado.lucroUnitario().valor()));
    }

    @Test
    void calculaMargemAPartirDoPreco() {
        var custo = brl("16.45");
        var resultado = PrecificacaoCalculos.calcularPorPreco(custo, brl("32.90"));
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado.margemPercentual()));
        assertEquals(0, new BigDecimal("16.45").compareTo(resultado.lucroUnitario().valor()));
    }

    @Test
    void alertaMargemNegativa() {
        var custo = brl("50");
        var resultado = PrecificacaoCalculos.calcularPorPreco(custo, brl("40"));
        assertEquals("MARGEM_NEGATIVA", resultado.alerta());
    }

    @Test
    void somaCustosDasLinhas() {
        var total = PrecificacaoCalculos.somarLinhas(
                java.util.List.of(brl("10.50"), brl("5.25"), brl("1.25")));
        assertEquals(0, new BigDecimal("17.00").compareTo(total.valor()));
    }

    @Test
    void rejeitaPrecoNegativo() {
        assertThrows(
                RegraDeNegocioViolada.class,
                () -> PrecificacaoCalculos.calcularPorPreco(brl("10"), brl("-1")));
    }

    @Test
    void custoZeroSemMargemDefinida() {
        var resultado = PrecificacaoCalculos.calcularPorPreco(Dinheiro.zero(), brl("49.90"));
        assertEquals(0, BigDecimal.ZERO.compareTo(resultado.margemPercentual()));
        assertEquals("CUSTO_ZERO", resultado.alerta());
    }
}