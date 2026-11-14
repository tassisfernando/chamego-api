package com.tassisfernando.chamegoapi.application.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.insumo.CategoriaInsumo;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarInsumoComando(
        UUID id,
        String nome,
        CategoriaInsumo categoria,
        String marca,
        UnidadeMedida unidadeMedida,
        BigDecimal estoqueAtual,
        BigDecimal custoMedio) {
}
