package com.tassisfernando.chamegoapi.application.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirDespesaUseCase {

    private final DespesaRepository despesaRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public ExcluirDespesaUseCase(
            DespesaRepository despesaRepository,
            InsumoRepository insumoRepository,
            MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.despesaRepository = despesaRepository;
        this.insumoRepository = insumoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public void executar(UUID despesaId) {
        Despesa despesa = despesaRepository
                .buscarPorId(despesaId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Despesa", despesaId));

        if (despesa.atualizaEstoqueInsumo()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(despesa.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", despesa.insumoId()));
            insumo.registrarSaida(despesa.quantidade());
            insumoRepository.salvar(insumo);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaInsumo(
                    insumo.id(),
                    despesa.quantidade(),
                    OrigemMovimento.CANCELAMENTO,
                    despesa.id(),
                    despesa.dataDespesa()));
        }

        despesaRepository.excluir(despesaId);
    }
}