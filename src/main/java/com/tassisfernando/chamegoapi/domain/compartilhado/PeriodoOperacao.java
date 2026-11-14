package com.tassisfernando.chamegoapi.domain.compartilhado;

import java.time.LocalDate;

public final class PeriodoOperacao {

    public static final LocalDate DATA_INICIO = LocalDate.of(2025, 12, 1);

    private PeriodoOperacao() {
    }

    public static LocalDate inicioEfetivo(LocalDate inicio) {
        if (inicio == null || inicio.isBefore(DATA_INICIO)) {
            return DATA_INICIO;
        }
        return inicio;
    }

    public static LocalDate fimEfetivo(LocalDate fim) {
        return fim == null ? LocalDate.now() : fim;
    }
}
