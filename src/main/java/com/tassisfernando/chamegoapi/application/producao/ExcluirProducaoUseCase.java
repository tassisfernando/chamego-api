package com.tassisfernando.chamegoapi.application.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.producao.ItemConsumoProducao;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import com.tassisfernando.chamegoapi.domain.producao.ProducaoRepository;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirProducaoUseCase {

    private final ProducaoRepository producaoRepository;
    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public ExcluirProducaoUseCase(
            ProducaoRepository producaoRepository,
            ProdutoRepository produtoRepository,
            InsumoRepository insumoRepository,
            MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.producaoRepository = producaoRepository;
        this.produtoRepository = produtoRepository;
        this.insumoRepository = insumoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public void executar(UUID loteId) {
        LoteProducao lote = producaoRepository
                .buscarPorId(loteId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Lote de produção", loteId));

        Produto produto = produtoRepository
                .buscarPorId(lote.produtoId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", lote.produtoId()));

        produto.sairEstoque(lote.quantidadeProduzida());
        produtoRepository.salvar(produto);
        movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                produto.id(),
                Quantidade.unidades(lote.quantidadeProduzida()),
                OrigemMovimento.CANCELAMENTO,
                lote.id(),
                lote.dataProducao()));

        for (ItemConsumoProducao consumo : lote.consumos()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(consumo.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", consumo.insumoId()));
            insumo.registrarEntrada(consumo.quantidade(), consumo.custoUnitario());
            insumoRepository.salvar(insumo);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaInsumo(
                    insumo.id(),
                    consumo.quantidade(),
                    OrigemMovimento.CANCELAMENTO,
                    lote.id(),
                    lote.dataProducao()));
        }

        producaoRepository.excluir(loteId);
    }
}