package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConverterOrcamentoEmVendaUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoEstoqueService pedidoEstoqueService;

    public ConverterOrcamentoEmVendaUseCase(
            PedidoRepository pedidoRepository, PedidoEstoqueService pedidoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoEstoqueService = pedidoEstoqueService;
    }

    @Transactional
    public Pedido executar(UUID pedidoId) {
        Pedido pedido = pedidoRepository
                .buscarPorId(pedidoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Pedido", pedidoId));
        if (!pedido.ehOrcamento()) {
            throw new RegraDeNegocioViolada("somente orçamentos podem ser concretizados em venda");
        }
        pedidoEstoqueService.aplicarSaidaEstoque(pedido);
        pedido.concretizarVenda();
        return pedidoRepository.salvar(pedido);
    }
}