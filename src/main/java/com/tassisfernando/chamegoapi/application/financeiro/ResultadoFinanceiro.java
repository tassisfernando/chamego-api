package com.tassisfernando.chamegoapi.application.financeiro;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;

import java.time.LocalDate;

public record ResultadoFinanceiro(
        LocalDate inicio,
        LocalDate fim,
        Dinheiro receitaVendas,
        Dinheiro custoMercadoriaVendida,
        Dinheiro lucroBruto,
        Dinheiro totalDespesas,
        Dinheiro resultadoLiquido) {
}
