package com.tassisfernando.chamegoapi.domain.compartilhado;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DinheiroTest {

    @Test
    void devePermitirResultadoFinanceiroNegativo() {
        Dinheiro lucroBruto = Dinheiro.de(new BigDecimal("100.00"));
        Dinheiro despesas = Dinheiro.de(new BigDecimal("252.01"));

        assertEquals(Dinheiro.de(new BigDecimal("-152.01")), lucroBruto.subtrair(despesas));
    }
}
