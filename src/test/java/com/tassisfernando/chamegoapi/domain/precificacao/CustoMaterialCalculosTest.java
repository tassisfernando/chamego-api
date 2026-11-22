package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustoMaterialCalculosTest {

    @Test
    void custoUnitarioUmPote() {
        var custo = CustoMaterialCalculos.custoUnitario(brl("2.00"), new BigDecimal("1"));
        assertEquals(0, new BigDecimal("2.00").compareTo(custo.valor()));
    }

    @Test
    void custoFracionadoFitaMetroParaCentimetro() {
        var custo = CustoMaterialCalculos.custoFracionado(
                brl("20.00"), new BigDecimal("5"), UnidadeMedida.MT, new BigDecimal("40"), UnidadeMedida.CM);
        assertEquals(0, new BigDecimal("1.60").compareTo(custo.valor()));
    }

    @Test
    void custoFracionadoCeraKgParaGramas() {
        var custo = CustoMaterialCalculos.custoFracionado(
                brl("40.00"), new BigDecimal("1"), UnidadeMedida.KG, new BigDecimal("100"), UnidadeMedida.G);
        assertEquals(0, new BigDecimal("4.00").compareTo(custo.valor()));
    }

    @Test
    void rejeitaUnidadesIncompativeis() {
        assertThrows(
                RegraDeNegocioViolada.class,
                () -> CustoMaterialCalculos.custoFracionado(
                        brl("10"), new BigDecimal("1"), UnidadeMedida.KG, new BigDecimal("40"), UnidadeMedida.CM));
    }
}