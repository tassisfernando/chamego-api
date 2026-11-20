package com.tassisfernando.chamegoapi.application.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeEmUso;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirInsumoUseCase {

    private final InsumoRepository insumoRepository;

    public ExcluirInsumoUseCase(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @Transactional
    public void executar(UUID insumoId) {
        insumoRepository
                .buscarPorId(insumoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", insumoId));
        if (insumoRepository.possuiReferencias(insumoId)) {
            throw new EntidadeEmUso(
                    "Não é possível excluir este insumo porque ele está na receita de um produto, em despesas ou em produção.");
        }
        insumoRepository.excluir(insumoId);
    }
}