package com.tassisfernando.chamegoapi.domain.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import org.junit.jupiter.api.Test;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InsumoTest {

    @Test
    void deveAtualizarCustoMedioNaEntradaDeEstoque() {
        Insumo insumo = Insumo.novo("CERA DE COCO", CategoriaInsumo.CERA, "SOLVEN", UnidadeMedida.G);

        insumo.registrarEntrada(quantidade("1000", UnidadeMedida.G), brl("10.00"));
        insumo.registrarEntrada(quantidade("1000", UnidadeMedida.G), brl("20.00"));

        assertEquals(quantidade("2000", UnidadeMedida.G), insumo.estoqueAtual());
        assertEquals(brl("15.00"), insumo.custoMedio());
    }

    @Test
    void deveRecusarSaidaQuandoEstoqueForInsuficiente() {
        Insumo insumo = ceraComEstoque("100", "4.20");

        assertThrows(
                RegraDeNegocioViolada.class,
                () -> insumo.registrarSaida(quantidade("150", UnidadeMedida.G)));
    }
}
