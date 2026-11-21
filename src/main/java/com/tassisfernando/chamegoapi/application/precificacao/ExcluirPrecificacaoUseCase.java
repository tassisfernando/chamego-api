package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirPrecificacaoUseCase {

    private final PrecificacaoRepository precificacaoRepository;

    public ExcluirPrecificacaoUseCase(PrecificacaoRepository precificacaoRepository) {
        this.precificacaoRepository = precificacaoRepository;
    }

    @Transactional
    public void executar(UUID id) {
        if (precificacaoRepository.buscarPorId(id).isEmpty()) {
            throw EntidadeNaoEncontrada.de("Precificação", id);
        }
        precificacaoRepository.excluir(id);
    }
}