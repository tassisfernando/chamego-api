package com.tassisfernando.chamegoapi.application.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarDespesaUseCase {

    private final DespesaRepository despesaRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public AtualizarDespesaUseCase(
            DespesaRepository despesaRepository,
            InsumoRepository insumoRepository,
            MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.despesaRepository = despesaRepository;
        this.insumoRepository = insumoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public Despesa executar(AtualizarDespesaComando comando) {
        Despesa atual = despesaRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Despesa", comando.id()));

        if (atual.atualizaEstoqueInsumo()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(atual.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", atual.insumoId()));
            insumo.registrarSaida(atual.quantidade());
            insumoRepository.salvar(insumo);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaInsumo(
                    insumo.id(),
                    atual.quantidade(),
                    OrigemMovimento.AJUSTE,
                    atual.id(),
                    atual.dataDespesa()));
        }

        Quantidade quantidade = comando.quantidade() == null || comando.unidadeMedida() == null
                ? null
                : Quantidade.de(comando.quantidade(), comando.unidadeMedida());

        atual.atualizar(
                comando.descricao(),
                comando.dataDespesa(),
                Dinheiro.de(comando.valor()),
                quantidade,
                comando.tipo(),
                comando.insumoId(),
                comando.ferramentaId(),
                comando.observacao());

        if (atual.atualizaEstoqueInsumo()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(atual.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", atual.insumoId()));
            insumo.registrarEntrada(atual.quantidade(), atual.custoUnitario());
            insumoRepository.salvar(insumo);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaInsumo(
                    insumo.id(),
                    atual.quantidade(),
                    OrigemMovimento.COMPRA,
                    atual.id(),
                    atual.dataDespesa()));
        }

        return despesaRepository.salvar(atual);
    }
}
