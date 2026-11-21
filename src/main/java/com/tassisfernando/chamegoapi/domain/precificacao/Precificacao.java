package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Precificacao {

    private final UUID id;
    private String titulo;
    private TipoPrecificacao tipo;
    private UUID produtoReferenciaId;
    private UUID produtoPublicadoId;
    private BigDecimal margemPercentual;
    private Dinheiro precoVenda;
    private Dinheiro custoTotal;
    private Dinheiro lucroUnitario;
    private String observacao;
    private final Instant criadoEm;
    private Instant atualizadoEm;
    private final List<ItemPrecificacao> itens;

    private Precificacao(
            UUID id,
            String titulo,
            TipoPrecificacao tipo,
            UUID produtoReferenciaId,
            UUID produtoPublicadoId,
            BigDecimal margemPercentual,
            Dinheiro precoVenda,
            Dinheiro custoTotal,
            Dinheiro lucroUnitario,
            String observacao,
            Instant criadoEm,
            Instant atualizadoEm,
            List<ItemPrecificacao> itens) {
        if (titulo == null || titulo.isBlank()) {
            throw new RegraDeNegocioViolada("título da precificação é obrigatório");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.tipo = tipo;
        this.produtoReferenciaId = produtoReferenciaId;
        this.produtoPublicadoId = produtoPublicadoId;
        this.margemPercentual = margemPercentual;
        this.precoVenda = precoVenda;
        this.custoTotal = custoTotal;
        this.lucroUnitario = lucroUnitario;
        this.observacao = observacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
        this.itens = new ArrayList<>(itens);
    }

    public static Precificacao nova(
            String titulo,
            TipoPrecificacao tipo,
            UUID produtoReferenciaId,
            List<ItemPrecificacao> itens,
            PrecificacaoCalculos.ResultadoMargemPreco resultado,
            String observacao) {
        Instant agora = Instant.now();
        return new Precificacao(
                UUID.randomUUID(),
                titulo,
                tipo,
                produtoReferenciaId,
                null,
                resultado.margemPercentual(),
                resultado.precoVenda(),
                resultado.custoTotal(),
                resultado.lucroUnitario(),
                observacao,
                agora,
                agora,
                itens);
    }

    public static Precificacao reconstituir(
            UUID id,
            String titulo,
            TipoPrecificacao tipo,
            UUID produtoReferenciaId,
            UUID produtoPublicadoId,
            BigDecimal margemPercentual,
            Dinheiro precoVenda,
            Dinheiro custoTotal,
            Dinheiro lucroUnitario,
            String observacao,
            Instant criadoEm,
            Instant atualizadoEm,
            List<ItemPrecificacao> itens) {
        return new Precificacao(
                id,
                titulo,
                tipo,
                produtoReferenciaId,
                produtoPublicadoId,
                margemPercentual,
                precoVenda,
                custoTotal,
                lucroUnitario,
                observacao,
                criadoEm,
                atualizadoEm,
                itens);
    }

    public void atualizar(
            String titulo,
            UUID produtoReferenciaId,
            List<ItemPrecificacao> itens,
            PrecificacaoCalculos.ResultadoMargemPreco resultado,
            String observacao) {
        if (titulo == null || titulo.isBlank()) {
            throw new RegraDeNegocioViolada("título da precificação é obrigatório");
        }
        this.titulo = titulo.trim();
        this.produtoReferenciaId = produtoReferenciaId;
        this.margemPercentual = resultado.margemPercentual();
        this.precoVenda = resultado.precoVenda();
        this.custoTotal = resultado.custoTotal();
        this.lucroUnitario = resultado.lucroUnitario();
        this.observacao = observacao;
        this.itens.clear();
        this.itens.addAll(itens);
        this.atualizadoEm = Instant.now();
    }

    public void vincularProdutoPublicado(UUID produtoId) {
        this.produtoPublicadoId = produtoId;
        this.atualizadoEm = Instant.now();
    }

    public UUID id() {
        return id;
    }

    public String titulo() {
        return titulo;
    }

    public TipoPrecificacao tipo() {
        return tipo;
    }

    public UUID produtoReferenciaId() {
        return produtoReferenciaId;
    }

    public UUID produtoPublicadoId() {
        return produtoPublicadoId;
    }

    public BigDecimal margemPercentual() {
        return margemPercentual;
    }

    public Dinheiro precoVenda() {
        return precoVenda;
    }

    public Dinheiro custoTotal() {
        return custoTotal;
    }

    public Dinheiro lucroUnitario() {
        return lucroUnitario;
    }

    public String observacao() {
        return observacao;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    public Instant atualizadoEm() {
        return atualizadoEm;
    }

    public List<ItemPrecificacao> itens() {
        return List.copyOf(itens);
    }
}