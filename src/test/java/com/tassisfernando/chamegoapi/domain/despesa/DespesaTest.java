package com.tassisfernando.chamegoapi.domain.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DespesaTest {

    @Test
    void deveCalcularCustoUnitarioDaCompraDeInsumo() {
        Despesa despesa = Despesa.nova(
                "CERA DE COCO 1KG",
                LocalDate.now(),
                brl("42.00"),
                quantidade("1000", UnidadeMedida.G),
                TipoDespesa.INSUMO,
                UUID.randomUUID(),
                null,
                null);

        assertTrue(despesa.atualizaEstoqueInsumo());
        assertEquals(brl("0.04"), despesa.custoUnitario());
    }

    @Test
    void deveRecusarDespesaDeInsumoSemVinculo() {
        assertThrows(
                RegraDeNegocioViolada.class,
                () -> Despesa.nova(
                        "CERA",
                        LocalDate.now(),
                        brl("10.00"),
                        null,
                        TipoDespesa.INSUMO,
                        null,
                        null,
                        null));
    }
}
