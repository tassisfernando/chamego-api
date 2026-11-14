package com.tassisfernando.chamegoapi.infrastructure.web.pedido;

import com.tassisfernando.chamegoapi.application.pedido.AtualizarPedidoComando;
import com.tassisfernando.chamegoapi.application.pedido.AtualizarPedidoUseCase;
import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoComando;
import com.tassisfernando.chamegoapi.application.pedido.RegistrarPedidoUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.PeriodoOperacao;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import com.tassisfernando.chamegoapi.infrastructure.web.compartilhado.PaginaResposta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    private final AtualizarPedidoUseCase atualizarPedidoUseCase;
    private final PedidoRepository pedidoRepository;

    public PedidoController(
            RegistrarPedidoUseCase registrarPedidoUseCase,
            AtualizarPedidoUseCase atualizarPedidoUseCase,
            PedidoRepository pedidoRepository) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
        this.atualizarPedidoUseCase = atualizarPedidoUseCase;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public PaginaResposta<PedidoResposta> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @PageableDefault(size = 20, sort = "dataPedido") Pageable pageable) {
        LocalDate inicioResolvido = PeriodoOperacao.inicioEfetivo(inicio);
        LocalDate fimResolvido = PeriodoOperacao.fimEfetivo(fim);
        return PaginaResposta.mapear(
                pedidoRepository.listarPorPeriodo(
                        inicioResolvido, fimResolvido, pageable.getPageNumber(), pageable.getPageSize()),
                PedidoResposta::de);
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

    @PutMapping("/{id}")
    public PedidoResposta atualizar(
            @PathVariable UUID id, @Valid @RequestBody AtualizarPedidoRequisicao requisicao) {
        Pedido pedido = atualizarPedidoUseCase.executar(new AtualizarPedidoComando(
                id,
                requisicao.clienteId(),
                requisicao.dataPedido(),
                requisicao.natureza(),
                requisicao.status(),
                requisicao.observacao(),
                requisicao.itens().stream().map(RegistrarPedidoRequisicao.ItemRequisicao::paraComando).toList()));
        return PedidoResposta.de(pedido);
    }

    public record AtualizarPedidoRequisicao(
            @NotNull UUID clienteId,
            LocalDate dataPedido,
            NaturezaPedido natureza,
            StatusPedido status,
            String observacao,
            @NotEmpty @Valid List<RegistrarPedidoRequisicao.ItemRequisicao> itens) {
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
