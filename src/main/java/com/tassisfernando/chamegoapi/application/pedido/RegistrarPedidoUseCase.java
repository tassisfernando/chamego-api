package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public RegistrarPedidoUseCase(
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
    public Pedido executar(RegistrarPedidoComando comando) {
        clienteRepository
                .buscarPorId(comando.clienteId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Cliente", comando.clienteId()));

        Pedido pedido = Pedido.novo(
                comando.clienteId(), comando.dataPedido(), comando.natureza(), comando.observacao());

        for (ItemPedidoComando itemComando : comando.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(itemComando.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", itemComando.produtoId()));

            Dinheiro preco = itemComando.precoUnitario() == null
                    ? produto.precoVenda()
                    : Dinheiro.de(itemComando.precoUnitario());

            pedido.adicionarItem(new ItemPedido(
                    produto.id(), itemComando.quantidade(), preco, produto.custoPadrao()));

            produto.sairEstoque(itemComando.quantidade());
            produtoRepository.salvar(produto);

            movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaProduto(
                    produto.id(),
                    Quantidade.unidades(itemComando.quantidade()),
                    OrigemMovimento.VENDA,
                    pedido.id(),
                    pedido.dataPedido()));
        }

        pedido.confirmar();
        return pedidoRepository.salvar(pedido);
    }
}
