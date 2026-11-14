package com.tassisfernando.chamegoapi.infrastructure.web.compartilhado;

import com.tassisfernando.chamegoapi.domain.compartilhado.PeriodoOperacao;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Parâmetros opcionais de período: omitidos = todo o período desde o início da operação. */
public record PeriodoRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {

    public LocalDate inicioResolvido() {
        return PeriodoOperacao.inicioEfetivo(inicio);
    }

    public LocalDate fimResolvido() {
        return PeriodoOperacao.fimEfetivo(fim);
    }
}
