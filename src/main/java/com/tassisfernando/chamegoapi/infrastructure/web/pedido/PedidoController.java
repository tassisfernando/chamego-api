package com.tassisfernando.chamegoapi.infrastructure.web.pedido;

import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoComando;
import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoUseCase;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final RegistrarPedidoUseCase registrarPedidoUseCase;
    private final PedidoRepository pedidoRepository;

    public PedidoController(
            RegistrarPedidoUseCase registrarPedidoUseCase, PedidoRepository pedidoRepository) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public List<PedidoResposta> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return pedidoRepository.listarPorPeriodo(inicio, fim).stream().map(PedidoResposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registrar(@Valid @RequestBody RegistrarPedidoRequisicao requisicao) {
        Pedido pedido = registrarPedidoUseCase.executar(new RegistrarPedidoComando(
                requisicao.clienteId(),
                requisicao.dataPedido(),
                requisicao.natureza() == null ? NaturezaPedido.VENDA : requisicao.natureza(),
                requisicao.observacao(),
                requisicao.itens().stream().map(RegistrarPedidoRequisicao.ItemRequisicao::paraComando).toList()));
        return Map.of(
                "id", pedido.id(),
                "status", pedido.status(),
                "valorTotal", pedido.valorTotal().valor(),
                "lucroBruto", pedido.lucroBruto().valor());
    }

    public record PedidoResposta(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            BigDecimal valorTotal,
            BigDecimal lucroBruto,
            List<ItemResposta> itens) {

        static PedidoResposta de(Pedido pedido) {
            return new PedidoResposta(
                    pedido.id(),
                    pedido.clienteId(),
                    pedido.dataPedido(),
                    pedido.status(),
                    pedido.natureza(),
                    pedido.observacao(),
                    pedido.valorTotal().valor(),
                    pedido.lucroBruto().valor(),
                    pedido.itens().stream().map(ItemResposta::de).toList());
        }
    }

    public record ItemResposta(
            UUID produtoId, int quantidade, BigDecimal precoUnitario, BigDecimal custoUnitario) {

        static ItemResposta de(ItemPedido item) {
            return new ItemResposta(
                    item.produtoId(),
                    item.quantidade(),
                    item.precoUnitario().valor(),
                    item.custoUnitario().valor());
        }
    }
}
