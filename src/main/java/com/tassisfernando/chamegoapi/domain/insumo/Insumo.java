package com.tassisfernando.chamegoapi.domain.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

public final class Insumo {

    private final UUID id;
    private String nome;
    private CategoriaInsumo categoria;
    private String marca;
    private UnidadeMedida unidadeMedida;
    private Quantidade estoqueAtual;
    private Dinheiro custoMedio;
    private boolean ativo;

    private Insumo(
            UUID id,
            String nome,
            CategoriaInsumo categoria,
            String marca,
            UnidadeMedida unidadeMedida,
            Quantidade estoqueAtual,
            Dinheiro custoMedio,
            boolean ativo) {
        this.id = id;
        this.nome = exigirNome(nome);
        this.categoria = Objects.requireNonNull(categoria);
        this.marca = marca;
        this.unidadeMedida = Objects.requireNonNull(unidadeMedida);
        this.estoqueAtual = estoqueAtual;
        this.custoMedio = custoMedio;
        this.ativo = ativo;
    }

    public static Insumo novo(
            String nome, CategoriaInsumo categoria, String marca, UnidadeMedida unidadeMedida) {
        return new Insumo(
                UUID.randomUUID(),
                nome,
                categoria,
                marca,
                unidadeMedida,
                Quantidade.de(BigDecimal.ZERO, unidadeMedida),
                Dinheiro.zero(),
                true);
    }

    public static Insumo reconstituir(
            UUID id,
            String nome,
            CategoriaInsumo categoria,
            String marca,
            UnidadeMedida unidadeMedida,
            Quantidade estoqueAtual,
            Dinheiro custoMedio,
            boolean ativo) {
        return new Insumo(id, nome, categoria, marca, unidadeMedida, estoqueAtual, custoMedio, ativo);
    }

    public void registrarEntrada(Quantidade quantidade, Dinheiro custoUnitario) {
        BigDecimal estoqueAnterior = estoqueAtual.valor();
        BigDecimal quantidadeEntrada = quantidade.valor();
        BigDecimal novoEstoque = estoqueAnterior.add(quantidadeEntrada);

        if (novoEstoque.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal valorAnterior = estoqueAnterior.multiply(custoMedio.valor());
            BigDecimal valorNovo = quantidadeEntrada.multiply(custoUnitario.valor());
            BigDecimal media = valorAnterior.add(valorNovo)
                    .divide(novoEstoque, 4, RoundingMode.HALF_UP);
            this.custoMedio = Dinheiro.de(media);
        }

        this.estoqueAtual = estoqueAtual.somar(quantidade);
    }

    public void registrarSaida(Quantidade quantidade) {
        if (estoqueAtual.menorQue(quantidade)) {
            throw new RegraDeNegocioViolada(
                    "estoque insuficiente do insumo " + nome + ": disponível "
                            + estoqueAtual.valor() + " " + unidadeMedida);
        }
        this.estoqueAtual = estoqueAtual.subtrair(quantidade);
    }

    public void atualizarDados(String nome, CategoriaInsumo categoria, String marca, UnidadeMedida unidadeMedida) {
        this.nome = exigirNome(nome);
        this.categoria = Objects.requireNonNull(categoria);
        this.marca = marca;
        if (unidadeMedida != null) {
            this.unidadeMedida = unidadeMedida;
        }
    }

    public void definirEstoque(Quantidade novoEstoque) {
        if (novoEstoque == null) {
            throw new RegraDeNegocioViolada("estoque do insumo é obrigatório");
        }
        this.estoqueAtual = novoEstoque;
    }

    public void definirCustoMedio(Dinheiro custoMedio) {
        this.custoMedio = custoMedio == null ? Dinheiro.zero() : custoMedio;
    }

    private static String exigirNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioViolada("nome do insumo é obrigatório");
        }
        return nome.trim();
    }

    public UUID id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public CategoriaInsumo categoria() {
        return categoria;
    }

    public String marca() {
        return marca;
    }

    public UnidadeMedida unidadeMedida() {
        return unidadeMedida;
    }

    public Quantidade estoqueAtual() {
        return estoqueAtual;
    }

    public Dinheiro custoMedio() {
        return custoMedio;
    }

    public boolean ativo() {
        return ativo;
    }
}
