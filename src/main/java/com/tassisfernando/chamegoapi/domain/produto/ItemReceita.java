package com.tassisfernando.chamegoapi.domain.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.util.UUID;

public record ItemReceita(UUID insumoId, Quantidade quantidadePorUnidade) {

    public ItemReceita {
        if (insumoId == null) {
            throw new RegraDeNegocioViolada("insumo da receita é obrigatório");
        }
        if (quantidadePorUnidade == null || quantidadePorUnidade.valor().signum() <= 0) {
            throw new RegraDeNegocioViolada("quantidade da receita deve ser positiva");
        }
    }

    public Quantidade quantidadePara(int unidadesProduzidas) {
        return Quantidade.de(
                quantidadePorUnidade.valor().multiply(java.math.BigDecimal.valueOf(unidadesProduzidas)),
                quantidadePorUnidade.unidade());
    }
}
