package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.pedido.AgendaEntregas;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ListarEntregasAgendadasUseCase {

    public static final ZoneId FUSO_OPERACAO = ZoneId.of("America/Sao_Paulo");
    public static final int HORIZONTE_PADRAO_DIAS = 7;

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public ListarEntregasAgendadasUseCase(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public PainelEntregas executar() {
        return executar(LocalDate.now(FUSO_OPERACAO), HORIZONTE_PADRAO_DIAS);
    }

    @Transactional(readOnly = true)
    public PainelEntregas executar(LocalDate hoje, int horizonteDias) {
        int dias = horizonteDias < 1 ? HORIZONTE_PADRAO_DIAS : Math.min(horizonteDias, 30);
        List<Pedido> pedidos = pedidoRepository.listarPorDataEntrega(hoje, hoje.plusDays(dias));
        Map<UUID, String> nomesProduto = new HashMap<>();
        List<EntregaAgendadaResumo> deHoje = new ArrayList<>();
        List<EntregaAgendadaResumo> proximas = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            if (AgendaEntregas.entregaHoje(pedido, hoje)) {
                deHoje.add(resumir(pedido, hoje, nomesProduto));
            } else if (AgendaEntregas.entregaProxima(pedido, hoje, dias)) {
                proximas.add(resumir(pedido, hoje, nomesProduto));
            }
        }
        return new PainelEntregas(List.copyOf(deHoje), List.copyOf(proximas));
    }

    private EntregaAgendadaResumo resumir(Pedido pedido, LocalDate hoje, Map<UUID, String> nomesProduto) {
        Cliente cliente = clienteRepository.buscarPorId(pedido.clienteId()).orElse(null);
        long dias = ChronoUnit.DAYS.between(hoje, pedido.dataEntrega());
        return new EntregaAgendadaResumo(
                pedido.id(),
                pedido.clienteId(),
                cliente == null ? "Cliente" : cliente.nome(),
                cliente == null ? null : cliente.telefone(),
                pedido.dataEntrega(),
                pedido.status(),
                resumoItens(pedido, nomesProduto),
                pedido.valorTotal().valor(),
                dias == 0,
                (int) dias);
    }

    private String resumoItens(Pedido pedido, Map<UUID, String> nomesProduto) {
        List<String> partes = new ArrayList<>();
        for (ItemPedido item : pedido.itens()) {
            String nome = nomesProduto.computeIfAbsent(item.produtoId(), id -> produtoRepository
                    .buscarPorId(id)
                    .map(produto -> produto.nome())
                    .orElse("Produto"));
            partes.add(item.quantidade() + "× " + nome);
        }
        return partes.isEmpty() ? "Sem itens" : String.join(", ", partes);
    }
}
