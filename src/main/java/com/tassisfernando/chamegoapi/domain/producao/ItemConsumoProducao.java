package com.tassisfernando.chamegoapi.domain.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;

import java.util.UUID;

public record ItemConsumoProducao(UUID insumoId, Quantidade quantidade, Dinheiro custoUnitario) {

    public Dinheiro custoTotal() {
        return custoUnitario.multiplicar(quantidade.valor());
    }
}
