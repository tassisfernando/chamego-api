package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AtualizarProdutoUseCase {

    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public AtualizarProdutoUseCase(
            ProdutoRepository produtoRepository, MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public Produto executar(AtualizarProdutoComando comando) {
        Produto produto = produtoRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", comando.id()));

        produto.atualizarDados(
                comando.nome(),
                comando.descricao(),
                comando.categoria(),
                comando.pesoGramas(),
                Dinheiro.de(comando.precoVenda() == null ? produto.precoVenda().valor() : comando.precoVenda()),
                Dinheiro.de(comando.custoPadrao() == null ? produto.custoPadrao().valor() : comando.custoPadrao()));

        if (comando.estoqueAtual() != null && comando.estoqueAtual() != produto.estoqueAtual()) {
            int anterior = produto.estoqueAtual();
            int novo = comando.estoqueAtual();
            int delta = novo - anterior;
            produto.definirEstoque(novo);
            if (delta > 0) {
                movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaProduto(
                        produto.id(),
                        Quantidade.unidades(delta),
                        OrigemMovimento.AJUSTE,
                        produto.id(),
                        LocalDate.now()));
            } else if (delta < 0) {
                movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                        produto.id(),
                        Quantidade.unidades(-delta),
                        OrigemMovimento.AJUSTE,
                        produto.id(),
                        LocalDate.now()));
            }
        }

        return produtoRepository.salvar(produto);
    }
}
