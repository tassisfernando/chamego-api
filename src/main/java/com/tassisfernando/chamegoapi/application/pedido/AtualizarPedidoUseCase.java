package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AtualizarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public AtualizarPedidoUseCase(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public Pedido executar(AtualizarPedidoComando comando) {
        Pedido atual = pedidoRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Pedido", comando.id()));

        if (atual.status() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioViolada("pedido cancelado não pode ser editado");
        }

        clienteRepository
                .buscarPorId(comando.clienteId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Cliente", comando.clienteId()));

        if (atual.status() == StatusPedido.CONFIRMADO) {
            reverterSaidaEstoque(atual);
        }

        StatusPedido novoStatus = comando.status() == null ? StatusPedido.CONFIRMADO : comando.status();
        if (novoStatus == StatusPedido.RASCUNHO) {
            throw new RegraDeNegocioViolada("pedido editado não pode voltar para rascunho");
        }

        List<ItemPedido> novosItens = new ArrayList<>();
        for (ItemPedidoComando itemComando : comando.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(itemComando.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", itemComando.produtoId()));
            Dinheiro preco = itemComando.precoUnitario() == null
                    ? produto.precoVenda()
                    : Dinheiro.de(itemComando.precoUnitario());
            novosItens.add(new ItemPedido(produto.id(), itemComando.quantidade(), preco, produto.custoPadrao()));
        }

        if (novosItens.isEmpty()) {
            throw new RegraDeNegocioViolada("pedido precisa de pelo menos um item");
        }

        Pedido atualizado = Pedido.reconstituir(
                atual.id(),
                comando.clienteId(),
                comando.dataPedido() == null ? atual.dataPedido() : comando.dataPedido(),
                StatusPedido.RASCUNHO,
                comando.natureza() == null ? atual.natureza() : comando.natureza(),
                comando.observacao(),
                List.of());

        for (ItemPedido item : novosItens) {
            atualizado.adicionarItem(item);
        }

        if (novoStatus == StatusPedido.CONFIRMADO) {
            for (ItemPedido item : novosItens) {
                Produto produto = produtoRepository
                        .buscarPorId(item.produtoId())
                        .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", item.produtoId()));
                produto.sairEstoque(item.quantidade());
                produtoRepository.salvar(produto);
                movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                        produto.id(),
                        Quantidade.unidades(item.quantidade()),
                        OrigemMovimento.VENDA,
                        atualizado.id(),
                        atualizado.dataPedido()));
            }
            atualizado.confirmar();
        } else if (novoStatus == StatusPedido.CANCELADO) {
            atualizado.cancelar();
        }

        return pedidoRepository.salvar(atualizado);
    }

    private void reverterSaidaEstoque(Pedido pedido) {
        for (ItemPedido item : pedido.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(item.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", item.produtoId()));
            produto.entrarEstoque(item.quantidade());
            produtoRepository.salvar(produto);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaProduto(
                    produto.id(),
                    Quantidade.unidades(item.quantidade()),
                    OrigemMovimento.AJUSTE,
                    pedido.id(),
                    pedido.dataPedido()));
        }
    }
}
