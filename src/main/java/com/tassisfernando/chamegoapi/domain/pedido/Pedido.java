package com.tassisfernando.chamegoapi.domain.pedido;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
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
    private final List<ItemPedido> itens;

    private Pedido(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
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
        this.itens = new ArrayList<>(itens == null ? List.of() : itens);
    }

    public static Pedido novo(UUID clienteId, LocalDate dataPedido, NaturezaPedido natureza, String observacao) {
        return new Pedido(
                UUID.randomUUID(),
                clienteId,
                dataPedido,
                StatusPedido.RASCUNHO,
                natureza,
                observacao,
                List.of());
    }

    public static Pedido reconstituir(
            UUID id,
            UUID clienteId,
            LocalDate dataPedido,
            StatusPedido status,
            NaturezaPedido natureza,
            String observacao,
            List<ItemPedido> itens) {
        return new Pedido(id, clienteId, dataPedido, status, natureza, observacao, itens);
    }

    public void adicionarItem(ItemPedido item) {
        garantirAlteravel();
        itens.add(item);
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

    public void cancelar() {
        if (status == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioViolada("pedido já está cancelado");
        }
        this.status = StatusPedido.CANCELADO;
    }

    public Dinheiro valorTotal() {
        return itens.stream().map(ItemPedido::receitaBruta).reduce(Dinheiro.zero(), Dinheiro::somar);
    }

    public Dinheiro custoTotal() {
        return itens.stream().map(ItemPedido::custoTotal).reduce(Dinheiro.zero(), Dinheiro::somar);
    }

    public Dinheiro receitaContabil() {
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

    private void garantirAlteravel() {
        if (status != StatusPedido.RASCUNHO) {
            throw new RegraDeNegocioViolada("somente pedidos em rascunho podem ser alterados");
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

    public List<ItemPedido> itens() {
        return List.copyOf(itens);
    }
}
