package com.tassisfernando.chamegoapi.domain.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Produto {

    private final UUID id;
    private String nome;
    private String descricao;
    private CategoriaProduto categoria;
    private Integer pesoGramas;
    private Dinheiro precoVenda;
    private Dinheiro custoPadrao;
    private int estoqueAtual;
    private boolean ativo;
    private final List<ItemReceita> receita;

    private Produto(
            UUID id,
            String nome,
            String descricao,
            CategoriaProduto categoria,
            Integer pesoGramas,
            Dinheiro precoVenda,
            Dinheiro custoPadrao,
            int estoqueAtual,
            boolean ativo,
            List<ItemReceita> receita) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioViolada("nome do produto é obrigatório");
        }
        if (estoqueAtual < 0) {
            throw new RegraDeNegocioViolada("estoque do produto não pode ser negativo");
        }
        this.id = id;
        this.nome = nome.trim();
        this.descricao = descricao;
        this.categoria = categoria == null ? CategoriaProduto.VELA : categoria;
        this.pesoGramas = pesoGramas;
        this.precoVenda = precoVenda == null ? Dinheiro.zero() : precoVenda;
        this.custoPadrao = custoPadrao == null ? Dinheiro.zero() : custoPadrao;
        this.estoqueAtual = estoqueAtual;
        this.ativo = ativo;
        this.receita = new ArrayList<>(receita == null ? List.of() : receita);
    }

    public static Produto novo(
            String nome,
            CategoriaProduto categoria,
            Integer pesoGramas,
            Dinheiro precoVenda,
            Dinheiro custoPadrao) {
        return new Produto(
                UUID.randomUUID(),
                nome,
                null,
                categoria,
                pesoGramas,
                precoVenda,
                custoPadrao,
                0,
                true,
                List.of());
    }

    public static Produto reconstituir(
            UUID id,
            String nome,
            String descricao,
            CategoriaProduto categoria,
            Integer pesoGramas,
            Dinheiro precoVenda,
            Dinheiro custoPadrao,
            int estoqueAtual,
            boolean ativo,
            List<ItemReceita> receita) {
        return new Produto(
                id, nome, descricao, categoria, pesoGramas, precoVenda, custoPadrao, estoqueAtual, ativo, receita);
    }

    public void definirReceita(List<ItemReceita> itens) {
        receita.clear();
        receita.addAll(itens);
    }

    public void entrarEstoque(int quantidade) {
        garantirQuantidadePositiva(quantidade);
        this.estoqueAtual += quantidade;
    }

    public void sairEstoque(int quantidade) {
        garantirQuantidadePositiva(quantidade);
        if (estoqueAtual < quantidade) {
            throw new RegraDeNegocioViolada(
                    "estoque insuficiente do produto " + nome + ": disponível " + estoqueAtual);
        }
        this.estoqueAtual -= quantidade;
    }

    public void atualizarCustoPadrao(Dinheiro novoCusto) {
        this.custoPadrao = novoCusto;
    }

    public void atualizarDados(
            String nome,
            String descricao,
            CategoriaProduto categoria,
            Integer pesoGramas,
            Dinheiro precoVenda,
            Dinheiro custoPadrao) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioViolada("nome do produto é obrigatório");
        }
        this.nome = nome.trim();
        this.descricao = descricao;
        this.categoria = categoria == null ? CategoriaProduto.VELA : categoria;
        this.pesoGramas = pesoGramas;
        this.precoVenda = precoVenda == null ? Dinheiro.zero() : precoVenda;
        this.custoPadrao = custoPadrao == null ? Dinheiro.zero() : custoPadrao;
    }

    public void definirEstoque(int novoEstoque) {
        if (novoEstoque < 0) {
            throw new RegraDeNegocioViolada("estoque do produto não pode ser negativo");
        }
        this.estoqueAtual = novoEstoque;
    }

    private static void garantirQuantidadePositiva(int quantidade) {
        if (quantidade <= 0) {
            throw new RegraDeNegocioViolada("quantidade deve ser positiva");
        }
    }

    public UUID id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String descricao() {
        return descricao;
    }

    public CategoriaProduto categoria() {
        return categoria;
    }

    public Integer pesoGramas() {
        return pesoGramas;
    }

    public Dinheiro precoVenda() {
        return precoVenda;
    }

    public Dinheiro custoPadrao() {
        return custoPadrao;
    }

    public int estoqueAtual() {
        return estoqueAtual;
    }

    public boolean ativo() {
        return ativo;
    }

    public List<ItemReceita> receita() {
        return List.copyOf(receita);
    }
}
