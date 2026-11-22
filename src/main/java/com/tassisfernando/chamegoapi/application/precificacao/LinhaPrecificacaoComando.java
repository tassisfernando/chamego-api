package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.precificacao.ModoCalculoMaterial;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoItemPrecificacao;

import java.math.BigDecimal;
import java.util.UUID;

public record LinhaPrecificacaoComando(
        TipoItemPrecificacao tipo,
        UUID produtoId,
        UUID insumoId,
        String nomeMaterial,
        String descricao,
        ModoCalculoMaterial modoCalculo,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        BigDecimal custoUnitarioInformado,
        BigDecimal valorEmbalagem,
        BigDecimal quantidadeEmbalagem,
        UnidadeMedida unidadeEmbalagem) {}