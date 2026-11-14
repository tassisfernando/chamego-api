package com.tassisfernando.chamegoapi.domain.ferramenta;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class Ferramenta {

    private final UUID id;
    private String nome;
    private BigDecimal quantidade;
    private UnidadeMedida unidadeMedida;
    private Dinheiro valorAquisicao;
    private LocalDate dataCompra;
    private String observacao;
    private boolean ativo;

    private Ferramenta(
            UUID id,
            String nome,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            Dinheiro valorAquisicao,
            LocalDate dataCompra,
            String observacao,
            boolean ativo) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioViolada("nome da ferramenta é obrigatório");
        }
        this.id = id;
        this.nome = nome.trim();
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.valorAquisicao = valorAquisicao;
        this.dataCompra = dataCompra;
        this.observacao = observacao;
        this.ativo = ativo;
    }

    public static Ferramenta nova(
            String nome,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            Dinheiro valorAquisicao,
            LocalDate dataCompra,
            String observacao) {
        return new Ferramenta(
                UUID.randomUUID(),
                nome,
                quantidade,
                unidadeMedida,
                valorAquisicao,
                dataCompra,
                observacao,
                true);
    }

    public static Ferramenta reconstituir(
            UUID id,
            String nome,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            Dinheiro valorAquisicao,
            LocalDate dataCompra,
            String observacao,
            boolean ativo) {
        return new Ferramenta(
                id, nome, quantidade, unidadeMedida, valorAquisicao, dataCompra, observacao, ativo);
    }

    public UUID id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public BigDecimal quantidade() {
        return quantidade;
    }

    public UnidadeMedida unidadeMedida() {
        return unidadeMedida;
    }

    public Dinheiro valorAquisicao() {
        return valorAquisicao;
    }

    public LocalDate dataCompra() {
        return dataCompra;
    }

    public String observacao() {
        return observacao;
    }

    public boolean ativo() {
        return ativo;
    }
}
