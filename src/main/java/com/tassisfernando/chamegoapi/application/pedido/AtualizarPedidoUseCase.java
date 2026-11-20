package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
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
    private final PedidoEstoqueService pedidoEstoqueService;

    public AtualizarPedidoUseCase(
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

        if (atual.estaConfirmado()) {
            pedidoEstoqueService.reverterSaidaEstoque(atual);
        }

        StatusPedido novoStatus = comando.status() == null ? atual.status() : comando.status();
        if (novoStatus == StatusPedido.RASCUNHO) {
            throw new RegraDeNegocioViolada("pedido editado não pode voltar para rascunho");
        }

        List<ItemPedido> novosItens = montarItens(comando.itens());
        if (novosItens.isEmpty()) {
            throw new RegraDeNegocioViolada("pedido precisa de pelo menos um item");
        }

        StatusPedido statusBase =
                atual.ehOrcamento() && novoStatus != StatusPedido.CONFIRMADO
                        ? StatusPedido.ORCAMENTO
                        : StatusPedido.RASCUNHO;

        Pedido atualizado = Pedido.reconstituir(
                atual.id(),
                comando.clienteId(),
                comando.dataPedido() == null ? atual.dataPedido() : comando.dataPedido(),
                statusBase,
                comando.natureza() == null ? atual.natureza() : comando.natureza(),
                comando.observacao(),
                PercentualDesconto.de(comando.descontoPercentual()),
                List.of());

        for (ItemPedido item : novosItens) {
            atualizado.adicionarItem(item);
        }

        if (novoStatus == StatusPedido.CONFIRMADO) {
            pedidoEstoqueService.aplicarSaidaEstoque(atualizado);
            atualizado.confirmar();
        } else if (novoStatus == StatusPedido.ORCAMENTO) {
            atualizado.registrarComoOrcamento();
        } else if (novoStatus == StatusPedido.CANCELADO) {
            atualizado.cancelar();
        }

        return pedidoRepository.salvar(atualizado);
    }

    private List<ItemPedido> montarItens(List<ItemPedidoComando> itensComando) {
        List<ItemPedido> novosItens = new ArrayList<>();
        for (ItemPedidoComando itemComando : itensComando) {
            Produto produto = produtoRepository
                    .buscarPorId(itemComando.produtoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", itemComando.produtoId()));
            Dinheiro preco = itemComando.precoUnitario() == null
                    ? produto.precoVenda()
                    : Dinheiro.de(itemComando.precoUnitario());
            novosItens.add(new ItemPedido(
                    produto.id(),
                    itemComando.quantidade(),
                    preco,
                    produto.custoPadrao(),
                    PercentualDesconto.de(itemComando.descontoPercentual())));
        }
        return novosItens;
    }
}