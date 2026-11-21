package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoCalculos;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoPrecificacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class SalvarPrecificacaoUseCase {

    private final PrecificacaoRepository precificacaoRepository;
    private final SimularPrecificacaoUseCase simularPrecificacaoUseCase;

    public SalvarPrecificacaoUseCase(
            PrecificacaoRepository precificacaoRepository,
            SimularPrecificacaoUseCase simularPrecificacaoUseCase) {
        this.precificacaoRepository = precificacaoRepository;
        this.simularPrecificacaoUseCase = simularPrecificacaoUseCase;
    }

    @Transactional
    public Precificacao executar(SalvarPrecificacaoComando comando) {
        var simulacao = simularPrecificacaoUseCase.executar(new SimularPrecificacaoUseCase.SimularPrecificacaoComando(
                comando.itens(), comando.margemPercentual(), comando.precoVenda()));
        Precificacao entidade = Precificacao.nova(
                comando.titulo(),
                comando.tipo(),
                comando.produtoReferenciaId(),
                simulacao.itens(),
                simulacao.resultado(),
                comando.observacao());
        return precificacaoRepository.salvar(entidade);
    }

    public record SalvarPrecificacaoComando(
            String titulo,
            TipoPrecificacao tipo,
            UUID produtoReferenciaId,
            List<LinhaPrecificacaoComando> itens,
            BigDecimal margemPercentual,
            BigDecimal precoVenda,
            String observacao) {}
}