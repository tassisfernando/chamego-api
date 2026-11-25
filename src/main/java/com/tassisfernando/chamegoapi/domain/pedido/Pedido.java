package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.PercentualDesconto;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Pedido {

    private final UUID id;
    private final UUID clienteId;
    private LocalDate dataPedido;
    private StatusPedido status;
    private NaturezaPedido natureza;
    private String observacao;
    private PercentualDesconto descontoPercentual;
    private LocalDate dataEntrega;
    private final List<ItemPedido> itens;

    private Pedido(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            PercentualDesconto descontoPercentual,
            LocalDate dataEntrega,
            List<ItemPedido> itens) {
        if (clienteId == null) {
            throw new RegraDeNegocioViolada("cliente do pedido é obrigatório");
        }
        this.id = id;
        this.clienteId = clienteId;
        this.dataPedido = dataPedido == null ? LocalDate.now() : dataPedido;
        this.status = status == null ? StatusPedido.RASCUNHO : status;
        this.natureza = natureza == null ? NaturezaPedido.VENDA : natureza;
        this.observacao = observacao;
        this.descontoPercentual = descontoPercentual == null ? PercentualDesconto.zero() : descontoPercentual;
        this.dataEntrega = dataEntrega;
        this.itens = new ArrayList<>(itens == null ? List.of() : itens);
    }

    public static Pedido novo(UUID clienteId, LocalDate dataPedido, NaturezaPedido natureza, String observacao) {
        return novo(clienteId, dataPedido, natureza, observacao, null);
    }

    public static Pedido novo(
            UUID clienteId,
            LocalDate dataPedido,
            NaturezaPedido natureza,
            String observacao,
            LocalDate dataEntrega) {
        return new Pedido(
                UUID.randomUUID(),
                clienteId,
                dataPedido,
                StatusPedido.RASCUNHO,
                natureza,
                observacao,
                PercentualDesconto.zero(),
                dataEntrega,
                List.of());
    }

    public static Pedido reconstituir(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            PercentualDesconto descontoPercentual,
            List<ItemPedido> itens) {
        return reconstituir(
                id, clienteId, dataPedido, status, natureza, observacao, descontoPercentual, null, itens);
    }

    public static Pedido reconstituir(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            PercentualDesconto descontoPercentual,
            LocalDate dataEntrega,
            List<ItemPedido> itens) {
        return new Pedido(
                id,
                clienteId,
                dataPedido,
                status,
                natureza,
                observacao,
                descontoPercentual,
                dataEntrega,
                itens);
    }

    public void adicionarItem(ItemPedido item) {
        garantirAlteravel();
        itens.add(item);
    }

    public void definirDescontoPedido(PercentualDesconto desconto) {
        garantirAlteravel();
        this.descontoPercentual = desconto == null ? PercentualDesconto.zero() : desconto;
    }

    /** Data combinada com a cliente. Pode ser ajustada mesmo depois da confirmação. */
    public void definirDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public void confirmar() {
        if (itens.isEmpty()) {
            throw new RegraDeNegocioViolada("pedido sem itens não pode ser confirmado");
        }
        if (status == StatusPedido.CONFIRMADO) {
            throw new RegraDeNegocioViolada("pedido já está confirmado");
        }
        if (status == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioViolada("pedido cancelado não pode ser confirmado");
        }
        this.status = StatusPedido.CONFIRMADO;
    }

    public void registrarComoOrcamento() {
        if (itens.isEmpty()) {
            throw new RegraDeNegocioViolada("pedido sem itens não pode ser salvo como orçamento");
        }
        if (status == StatusPedido.CONFIRMADO) {
            throw new RegraDeNegocioViolada("venda confirmada não pode voltar para orçamento");
        }
        if (status == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioViolada("pedido cancelado não pode ser orçamento");
        }
        this.status = StatusPedido.ORCAMENTO;
    }

    public void concretizarVenda() {
        if (status != StatusPedido.ORCAMENTO) {
            throw new RegraDeNegocioViolada("somente orçamentos podem ser concretizados em venda");
        }
        confirmar();
    }

    public void cancelar() {
        if (status == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioViolada("pedido já está cancelado");
        }
        this.status = StatusPedido.CANCELADO;
    }

    public Dinheiro subtotalItens() {
        return PedidoCalculos.subtotalItens(itens);
    }

    public Dinheiro valorTotal() {
        return PedidoCalculos.valorTotal(itens, descontoPercentual);
    }

    public Dinheiro custoTotal() {
        return itens.stream().map(ItemPedido::custoTotal).reduce(Dinheiro.zero(), Dinheiro::somar);
    }

    public Dinheiro receitaContabil() {
        if (status == StatusPedido.ORCAMENTO) {
            return Dinheiro.zero();
        }
        return switch (natureza) {
            case DOACAO, PARCERIA -> Dinheiro.zero();
            case VENDA, CONTRIBUICAO -> valorTotal();
        };
    }

    public Dinheiro lucroBruto() {
        return receitaContabil().subtrair(custoTotal());
    }

    public boolean estaConfirmado() {
        return status == StatusPedido.CONFIRMADO;
    }

    public boolean ehOrcamento() {
        return status == StatusPedido.ORCAMENTO;
    }

    private void garantirAlteravel() {
        if (status != StatusPedido.RASCUNHO && status != StatusPedido.ORCAMENTO) {
            throw new RegraDeNegocioViolada("somente orçamentos ou rascunhos podem ser alterados");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID clienteId() {
        return clienteId;
    }

    public LocalDate dataPedido() {
        return dataPedido;
    }

    public StatusPedido status() {
        return status;
    }

    public NaturezaPedido natureza() {
        return natureza;
    }

    public String observacao() {
        return observacao;
    }

    public PercentualDesconto descontoPercentual() {
        return descontoPercentual;
    }

    public LocalDate dataEntrega() {
        return dataEntrega;
    }

    public List<ItemPedido> itens() {
        return List.copyOf(itens);
    }
}