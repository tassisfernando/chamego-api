package com.tassisfernando.chamegoapi.application.auth;

import com.tassisfernando.chamegoapi.domain.usuario.Usuario;

import java.time.Instant;

public interface GeradorTokenPort {

    String gerar(Usuario usuario);

    Instant expiracaoDe(String token);

    String extrairLogin(String token);

    boolean valido(String token);
}
