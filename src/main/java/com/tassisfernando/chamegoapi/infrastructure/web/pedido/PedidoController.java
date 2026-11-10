package com.tassisfernando.chamegoapi.infrastructure.web.pedido;

import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoComando;
import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoUseCase;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final RegistrarPedidoUseCase registrarPedidoUseCase;

    public PedidoController(RegistrarPedidoUseCase registrarPedidoUseCase) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
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
}
