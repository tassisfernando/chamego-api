package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.precificacao.ItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoCalculos;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SimularPrecificacaoUseCase {

    private final PrecificacaoMontagemService montagemService;

    public SimularPrecificacaoUseCase(PrecificacaoMontagemService montagemService) {
        this.montagemService = montagemService;
    }

    public ResultadoSimulacao executar(SimularPrecificacaoComando comando) {
        List<ItemPrecificacao> itens = montagemService.montarItens(comando.itens());
        Dinheiro custoTotal = montagemService.custoTotal(itens);
        PrecificacaoCalculos.ResultadoMargemPreco precificacao =
                resolverPrecificacao(custoTotal, comando.margemPercentual(), comando.precoVenda());
        return new ResultadoSimulacao(itens, precificacao);
    }

    private PrecificacaoCalculos.ResultadoMargemPreco resolverPrecificacao(
            Dinheiro custoTotal, BigDecimal margemPercentual, BigDecimal precoVenda) {
        if (precoVenda != null) {
            return PrecificacaoCalculos.calcularPorPreco(custoTotal, Dinheiro.de(precoVenda));
        }
        return PrecificacaoCalculos.calcularPorMargem(custoTotal, margemPercentual == null ? BigDecimal.ZERO : margemPercentual);
    }

    public record SimularPrecificacaoComando(
            List<LinhaPrecificacaoComando> itens, BigDecimal margemPercentual, BigDecimal precoVenda) {}

    public record ResultadoSimulacao(
            List<ItemPrecificacao> itens, PrecificacaoCalculos.ResultadoMargemPreco resultado) {}
}