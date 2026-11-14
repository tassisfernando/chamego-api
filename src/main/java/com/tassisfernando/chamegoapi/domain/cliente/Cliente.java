package com.tassisfernando.chamegoapi.domain.cliente;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Objects;
import java.util.UUID;

public final class Cliente {

    private final UUID id;
    private String nome;
    private String telefone;
    private String cidade;
    private String estado;
    private LocalDate dataCadastro;
    private LocalDate dataNascimento;
    private StatusCupom statusCupom;
    private String observacao;
    private boolean ativo;

    private Cliente(
            UUID id,
            String nome,
            String telefone,
            String cidade,
            String estado,
            LocalDate dataCadastro,
            LocalDate dataNascimento,
            StatusCupom statusCupom,
            String observacao,
            boolean ativo) {
        this.id = id;
        this.nome = exigirNome(nome);
        this.telefone = telefone;
        this.cidade = cidade;
        this.estado = estado;
        this.dataCadastro = dataCadastro == null ? LocalDate.now() : dataCadastro;
        this.dataNascimento = dataNascimento;
        this.statusCupom = statusCupom == null ? StatusCupom.NAO_DEFINIDO : statusCupom;
        this.observacao = observacao;
        this.ativo = ativo;
    }

    public static Cliente novo(
            String nome,
            String telefone,
            String cidade,
            String estado,
            LocalDate dataNascimento) {
        return new Cliente(
                UUID.randomUUID(),
                nome,
                telefone,
                cidade,
                estado,
                LocalDate.now(),
                dataNascimento,
                StatusCupom.NAO_DEFINIDO,
                null,
                true);
    }

    public static Cliente reconstituir(
            UUID id,
            String nome,
            String telefone,
            String cidade,
            String estado,
            LocalDate dataCadastro,
            LocalDate dataNascimento,
            StatusCupom statusCupom,
            String observacao,
            boolean ativo) {
        return new Cliente(
                id,
                nome,
                telefone,
                cidade,
                estado,
                dataCadastro,
                dataNascimento,
                statusCupom,
                observacao,
                ativo);
    }

    public void atualizarDados(
            String nome, String telefone, String cidade, String estado, LocalDate dataNascimento) {
        this.nome = exigirNome(nome);
        this.telefone = telefone;
        this.cidade = cidade;
        this.estado = estado;
        this.dataNascimento = dataNascimento;
    }

    public void desativar() {
        this.ativo = false;
    }

    public void reativar() {
        this.ativo = true;
    }

    public void marcarCupomEnviado() {
        this.statusCupom = StatusCupom.ENVIADO;
    }

    public void marcarCupomPendente() {
        this.statusCupom = StatusCupom.PENDENTE_ENVIO;
    }

    public boolean fazAniversarioEm(MonthDay dia) {
        return dataNascimento != null && MonthDay.from(dataNascimento).equals(dia);
    }

    public boolean temTelefone() {
        return telefone != null && !telefone.isBlank();
    }

    private static String exigirNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioViolada("nome do cliente é obrigatório");
        }
        return nome.trim();
    }

    public UUID id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public String telefone() {
        return telefone;
    }

    public String cidade() {
        return cidade;
    }

    public String estado() {
        return estado;
    }

    public LocalDate dataCadastro() {
        return dataCadastro;
    }

    public LocalDate dataNascimento() {
        return dataNascimento;
    }

    public StatusCupom statusCupom() {
        return statusCupom;
    }

    public String observacao() {
        return observacao;
    }

    public boolean ativo() {
        return ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cliente cliente)) {
            return false;
        }
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
