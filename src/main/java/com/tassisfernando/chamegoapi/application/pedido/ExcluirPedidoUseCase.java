package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final PedidoEstoqueService pedidoEstoqueService;

    public ExcluirPedidoUseCase(PedidoRepository pedidoRepository, PedidoEstoqueService pedidoEstoqueService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoEstoqueService = pedidoEstoqueService;
    }

    @Transactional
    public void executar(UUID pedidoId) {
        Pedido pedido = pedidoRepository
                .buscarPorId(pedidoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Pedido", pedidoId));
        if (pedido.estaConfirmado()) {
            pedidoEstoqueService.reverterSaidaEstoque(pedido);
        }
        pedidoRepository.excluir(pedidoId);
    }
}