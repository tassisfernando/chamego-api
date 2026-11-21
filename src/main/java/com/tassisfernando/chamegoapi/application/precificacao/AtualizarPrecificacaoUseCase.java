package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AtualizarPrecificacaoUseCase {

    private final PrecificacaoRepository precificacaoRepository;
    private final SimularPrecificacaoUseCase simularPrecificacaoUseCase;

    public AtualizarPrecificacaoUseCase(
            PrecificacaoRepository precificacaoRepository, SimularPrecificacaoUseCase simularPrecificacaoUseCase) {
        this.precificacaoRepository = precificacaoRepository;
        this.simularPrecificacaoUseCase = simularPrecificacaoUseCase;
    }

    @Transactional
    public Precificacao executar(AtualizarPrecificacaoComando comando) {
        Precificacao atual = precificacaoRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Precificação", comando.id()));
        var simulacao = simularPrecificacaoUseCase.executar(new SimularPrecificacaoUseCase.SimularPrecificacaoComando(
                comando.itens(), comando.margemPercentual(), comando.precoVenda()));
        atual.atualizar(
                comando.titulo(),
                comando.produtoReferenciaId(),
                simulacao.itens(),
                simulacao.resultado(),
                comando.observacao());
        return precificacaoRepository.salvar(atual);
    }

    public record AtualizarPrecificacaoComando(
            UUID id,
            String titulo,
            UUID produtoReferenciaId,
            List<LinhaPrecificacaoComando> itens,
            BigDecimal margemPercentual,
            BigDecimal precoVenda,
            String observacao) {}
}