package com.tassisfernando.chamegoapi.infrastructure.web.precificacao;

import com.tassisfernando.chamegoapi.application.precificacao.SimularPrecificacaoUseCase;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoCalculos;

import java.math.BigDecimal;
import java.util.List;

public record SimulacaoResposta(
        List<ItemResposta> itens,
        BigDecimal custoTotal,
        BigDecimal margemPercentual,
        BigDecimal precoVenda,
        BigDecimal lucroUnitario,
        String alerta) {

    static SimulacaoResposta de(SimularPrecificacaoUseCase.ResultadoSimulacao resultado) {
        PrecificacaoCalculos.ResultadoMargemPreco p = resultado.resultado();
        return new SimulacaoResposta(
                resultado.itens().stream().map(ItemResposta::de).toList(),
                p.custoTotal().valor(),
                p.margemPercentual(),
                p.precoVenda().valor(),
                p.lucroUnitario().valor(),
                p.alerta());
    }
}