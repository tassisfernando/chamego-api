package com.tassisfernando.chamegoapi.application.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import com.tassisfernando.chamegoapi.domain.producao.ItemConsumoProducao;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import com.tassisfernando.chamegoapi.domain.producao.ProducaoRepository;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AtualizarProducaoUseCase {

    private final ProducaoRepository producaoRepository;
    private final ProdutoRepository produtoRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public AtualizarProducaoUseCase(
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
    public LoteProducao executar(AtualizarProducaoComando comando) {
        LoteProducao atual = producaoRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produção", comando.id()));

        reverterLote(atual);

        Produto produto = produtoRepository
                .buscarPorId(comando.produtoId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", comando.produtoId()));

        if (produto.receita().isEmpty()) {
            throw new RegraDeNegocioViolada(
                    "produto " + produto.nome() + " não possui receita de insumos cadastrada");
        }

        List<ItemConsumoProducao> consumos = new ArrayList<>();
        for (ItemReceita itemReceita : produto.receita()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(itemReceita.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", itemReceita.insumoId()));
            var quantidadeConsumida = itemReceita.quantidadePara(comando.quantidadeProduzida());
            insumo.registrarSaida(quantidadeConsumida);
            insumoRepository.salvar(insumo);
            consumos.add(new ItemConsumoProducao(insumo.id(), quantidadeConsumida, insumo.custoMedio()));
            movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaInsumo(
                    insumo.id(),
                    quantidadeConsumida,
                    OrigemMovimento.PRODUCAO,
                    produto.id(),
                    comando.dataProducao()));
        }

        LoteProducao atualizado = LoteProducao.reconstituir(
                atual.id(),
                produto.id(),
                comando.dataProducao(),
                comando.quantidadeProduzida(),
                consumos,
                comando.observacao());

        produto.entrarEstoque(comando.quantidadeProduzida());
        produto.atualizarCustoPadrao(atualizado.custoUnitario());
        produtoRepository.salvar(produto);

        movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaProduto(
                produto.id(),
                Quantidade.unidades(comando.quantidadeProduzida()),
                OrigemMovimento.PRODUCAO,
                atualizado.id(),
                atualizado.dataProducao()));

        return producaoRepository.salvar(atualizado);
    }

    private void reverterLote(LoteProducao lote) {
        Produto produto = produtoRepository
                .buscarPorId(lote.produtoId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", lote.produtoId()));
        produto.sairEstoque(lote.quantidadeProduzida());
        produtoRepository.salvar(produto);
        movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                produto.id(),
                Quantidade.unidades(lote.quantidadeProduzida()),
                OrigemMovimento.AJUSTE,
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
                    OrigemMovimento.AJUSTE,
                    lote.id(),
                    lote.dataProducao()));
        }
    }
}
