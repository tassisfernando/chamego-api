package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;

@Service
public class PedidoEstoqueService {

    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public PedidoEstoqueService(
            ProdutoRepository produtoRepository, MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    public void aplicarSaidaEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(item.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", item.produtoId()));
            produto.sairEstoque(item.quantidade());
            produtoRepository.salvar(produto);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                    produto.id(),
                    Quantidade.unidades(item.quantidade()),
                    OrigemMovimento.VENDA,
                    pedido.id(),
                    pedido.dataPedido()));
        }
    }

    public void reverterSaidaEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(item.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", item.produtoId()));
            produto.entrarEstoque(item.quantidade());
            produtoRepository.salvar(produto);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaProduto(
                    produto.id(),
                    Quantidade.unidades(item.quantidade()),
                    OrigemMovimento.CANCELAMENTO,
                    pedido.id(),
                    pedido.dataPedido()));
        }
    }
}