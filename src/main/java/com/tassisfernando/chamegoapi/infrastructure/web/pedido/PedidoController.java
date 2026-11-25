package com.tassisfernando.chamegoapi.infrastructure.web.pedido;

import com.tassisfernando.chamegoapi.application.pedido.AtualizarPedidoComando;
import com.tassisfernando.chamegoapi.application.pedido.AtualizarPedidoUseCase;
import com.tassisfernando.chamegoapi.application.pedido.ConverterOrcamentoEmVendaUseCase;
import com.tassisfernando.chamegoapi.application.pedido.ExcluirPedidoUseCase;
import com.tassisfernando.chamegoapi.application.pedido.ListarEntregasAgendadasUseCase;
import com.tassisfernando.chamegoapi.application.pedido.PainelEntregas;
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
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final RegistrarPedidoUseCase registrarPedidoUseCase;
    private final AtualizarPedidoUseCase atualizarPedidoUseCase;
    private final ConverterOrcamentoEmVendaUseCase converterOrcamentoEmVendaUseCase;
    private final ExcluirPedidoUseCase excluirPedidoUseCase;
    private final PedidoRepository pedidoRepository;
    private final ListarEntregasAgendadasUseCase listarEntregasAgendadasUseCase;

    public PedidoController(
            RegistrarPedidoUseCase registrarPedidoUseCase,
            AtualizarPedidoUseCase atualizarPedidoUseCase,
            ConverterOrcamentoEmVendaUseCase converterOrcamentoEmVendaUseCase,
            ExcluirPedidoUseCase excluirPedidoUseCase,
            PedidoRepository pedidoRepository,
            ListarEntregasAgendadasUseCase listarEntregasAgendadasUseCase) {
        this.registrarPedidoUseCase = registrarPedidoUseCase;
        this.atualizarPedidoUseCase = atualizarPedidoUseCase;
        this.converterOrcamentoEmVendaUseCase = converterOrcamentoEmVendaUseCase;
        this.excluirPedidoUseCase = excluirPedidoUseCase;
        this.pedidoRepository = pedidoRepository;
        this.listarEntregasAgendadasUseCase = listarEntregasAgendadasUseCase;
    }

    @GetMapping("/entregas/painel")
    public PainelEntregas painelEntregas(@RequestParam(required = false) Integer horizonteDias) {
        return listarEntregasAgendadasUseCase.executar(
                LocalDate.now(ListarEntregasAgendadasUseCase.FUSO_OPERACAO),
                horizonteDias == null ? ListarEntregasAgendadasUseCase.HORIZONTE_PADRAO_DIAS : horizonteDias);
    }

    @GetMapping
    public PaginaResposta<PedidoResposta> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) StatusPedido status,
            @PageableDefault(size = 20, sort = "dataPedido") Pageable pageable) {
        LocalDate inicioResolvido = PeriodoOperacao.inicioEfetivo(inicio);
        LocalDate fimResolvido = PeriodoOperacao.fimEfetivo(fim);
        var pagina = pedidoRepository.listarPorPeriodo(
                inicioResolvido,
                fimResolvido,
                status,
                pageable.getPageNumber(),
                pageable.getPageSize());
        return PaginaResposta.mapear(pagina, PedidoResposta::de);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResposta registrar(@Valid @RequestBody RegistrarPedidoRequisicao requisicao) {
        Pedido pedido = registrarPedidoUseCase.executar(new RegistrarPedidoComando(
                requisicao.clienteId(),
                requisicao.dataPedido(),
                requisicao.natureza() == null ? NaturezaPedido.VENDA : requisicao.natureza(),
                requisicao.observacao(),
                requisicao.descontoPercentual(),
                requisicao.confirmarVendaEfetivo(),
                requisicao.itens().stream().map(RegistrarPedidoRequisicao.ItemRequisicao::paraComando).toList(),
                requisicao.dataEntrega()));
        return PedidoResposta.de(pedido);
    }

    @PostMapping("/{id}/concretizar")
    public PedidoResposta concretizar(@PathVariable UUID id) {
        return PedidoResposta.de(converterOrcamentoEmVendaUseCase.executar(id));
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
                requisicao.descontoPercentual(),
                requisicao.itens().stream().map(RegistrarPedidoRequisicao.ItemRequisicao::paraComando).toList(),
                requisicao.dataEntrega()));
        return PedidoResposta.de(pedido);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirPedidoUseCase.executar(id);
    }

    public record AtualizarPedidoRequisicao(
            @NotNull UUID clienteId,
            LocalDate dataPedido,
            NaturezaPedido natureza,
            StatusPedido status,
            String observacao,
            @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal descontoPercentual,
            LocalDate dataEntrega,
            @NotEmpty @Valid List<RegistrarPedidoRequisicao.ItemRequisicao> itens) {
    }

    public record PedidoResposta(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            BigDecimal descontoPercentual,
            BigDecimal subtotalItens,
            BigDecimal valorTotal,
            BigDecimal lucroBruto,
            LocalDate dataEntrega,
            List<ItemResposta> itens) {

        static PedidoResposta de(Pedido pedido) {
            return new PedidoResposta(
                    pedido.id(),
                    pedido.clienteId(),
                    pedido.dataPedido(),
                    pedido.status(),
                    pedido.natureza(),
                    pedido.observacao(),
                    pedido.descontoPercentual().valor(),
                    pedido.subtotalItens().valor(),
                    pedido.valorTotal().valor(),
                    pedido.lucroBruto().valor(),
                    pedido.dataEntrega(),
                    pedido.itens().stream().map(ItemResposta::de).toList());
        }
    }

    public record ItemResposta(
            UUID produtoId,
            int quantidade,
            BigDecimal precoUnitario,
            BigDecimal custoUnitario,
            BigDecimal descontoPercentual,
            BigDecimal totalLiquido) {

        static ItemResposta de(ItemPedido item) {
            return new ItemResposta(
                    item.produtoId(),
                    item.quantidade(),
                    item.precoUnitario().valor(),
                    item.custoUnitario().valor(),
                    item.descontoPercentual().valor(),
                    item.receitaBruta().valor());
        }
    }
}