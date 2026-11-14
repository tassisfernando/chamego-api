package com.tassisfernando.chamegoapi.domain.usuario;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public final class Usuario {

    private final UUID id;
    private final String login;
    private final String senhaHash;
    private boolean ativo;
    private final Instant criadoEm;

    private Usuario(UUID id, String login, String senhaHash, boolean ativo, Instant criadoEm) {
        if (login == null || login.isBlank()) {
            throw new RegraDeNegocioViolada("login do usuário é obrigatório");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new RegraDeNegocioViolada("senha do usuário é obrigatória");
        }
        this.id = id;
        this.login = login.trim().toLowerCase();
        this.senhaHash = senhaHash;
        this.ativo = ativo;
        this.criadoEm = criadoEm == null ? Instant.now() : criadoEm;
    }

    public static Usuario novo(String login, String senhaHash) {
        return new Usuario(UUID.randomUUID(), login, senhaHash, true, Instant.now());
    }

    public static Usuario reconstituir(
            UUID id, String login, String senhaHash, boolean ativo, Instant criadoEm) {
        return new Usuario(id, login, senhaHash, ativo, criadoEm);
    }

    public void desativar() {
        this.ativo = false;
    }

    public UUID id() {
        return id;
    }

    public String login() {
        return login;
    }

    public String senhaHash() {
        return senhaHash;
    }

    public boolean ativo() {
        return ativo;
    }

    public Instant criadoEm() {
        return criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario usuario)) {
            return false;
        }
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
