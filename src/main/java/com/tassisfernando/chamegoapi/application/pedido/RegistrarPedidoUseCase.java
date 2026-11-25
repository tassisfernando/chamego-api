package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;
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
    private final PedidoEstoqueService pedidoEstoqueService;

    public RegistrarPedidoUseCase(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            PedidoEstoqueService pedidoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoEstoqueService = pedidoEstoqueService;
    }

    @Transactional
    public Pedido executar(RegistrarPedidoComando comando) {
        clienteRepository
                .buscarPorId(comando.clienteId())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Cliente", comando.clienteId()));

        Pedido pedido = Pedido.novo(
                comando.clienteId(),
                comando.dataPedido(),
                comando.natureza(),
                comando.observacao(),
                comando.dataEntrega());
        pedido.definirDescontoPedido(PercentualDesconto.de(comando.descontoPercentual()));

        for (ItemPedidoComando itemComando : comando.itens()) {
            Produto produto = produtoRepository
                    .buscarPorId(itemComando.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", itemComando.produtoId()));

            Dinheiro preco = itemComando.precoUnitario() == null
                    ? produto.precoVenda()
                    : Dinheiro.de(itemComando.precoUnitario());

            pedido.adicionarItem(new ItemPedido(
                    produto.id(),
                    itemComando.quantidade(),
                    preco,
                    produto.custoPadrao(),
                    PercentualDesconto.de(itemComando.descontoPercentual())));
        }

        if (comando.confirmarVenda()) {
            pedido.confirmar();
            pedidoEstoqueService.aplicarSaidaEstoque(pedido);
        } else {
            pedido.registrarComoOrcamento();
        }

        return pedidoRepository.salvar(pedido);
    }
}