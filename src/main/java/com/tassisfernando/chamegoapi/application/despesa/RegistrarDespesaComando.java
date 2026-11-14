package com.tassisfernando.chamegoapi.application.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RegistrarDespesaComando(
        String descricao,
        LocalDate dataDespesa,
        BigDecimal valor,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        TipoDespesa tipo,
        UUID insumoId,
        UUID ferramentaId,
        String observacao) {
}
