package com.tassisfernando.chamegoapi.application.ferramenta;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeEmUso;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.ferramenta.FerramentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirFerramentaUseCase {

    private final FerramentaRepository ferramentaRepository;

    public ExcluirFerramentaUseCase(FerramentaRepository ferramentaRepository) {
        this.ferramentaRepository = ferramentaRepository;
    }

    @Transactional
    public void executar(UUID ferramentaId) {
        ferramentaRepository
                .buscarPorId(ferramentaId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Ferramenta", ferramentaId));
        if (ferramentaRepository.possuiReferencias(ferramentaId)) {
            throw new EntidadeEmUso(
                    "Não é possível excluir esta ferramenta porque ela está vinculada a despesas.");
        }
        ferramentaRepository.excluir(ferramentaId);
    }
}