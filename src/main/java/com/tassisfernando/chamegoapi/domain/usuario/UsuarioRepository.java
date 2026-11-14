package com.tassisfernando.chamegoapi.domain.usuario;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository {

    Usuario salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(UUID id);

    Optional<Usuario> buscarPorLogin(String login);

    long contar();
}
