package com.tassisfernando.chamegoapi.infrastructure.persistence.usuario;

import com.tassisfernando.chamegoapi.domain.usuario.Usuario;
import com.tassisfernando.chamegoapi.domain.usuario.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario salvar(Usuario usuario) {
        return paraDominio(jpaRepository.save(paraJpa(usuario)));
    }

    @Override
    public Optional<Usuario> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(this::paraDominio);
    }

    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return jpaRepository.findByLoginIgnoreCase(login).map(this::paraDominio);
    }

    @Override
    public long contar() {
        return jpaRepository.count();
    }

    private UsuarioJpaEntity paraJpa(Usuario usuario) {
        UsuarioJpaEntity entidade = jpaRepository.findById(usuario.id()).orElseGet(UsuarioJpaEntity::new);
        entidade.setId(usuario.id());
        entidade.setLogin(usuario.login());
        entidade.setSenhaHash(usuario.senhaHash());
        entidade.setAtivo(usuario.ativo());
        entidade.setCriadoEm(usuario.criadoEm());
        return entidade;
    }

    private Usuario paraDominio(UsuarioJpaEntity entidade) {
        return Usuario.reconstituir(
                entidade.getId(),
                entidade.getLogin(),
                entidade.getSenhaHash(),
                entidade.isAtivo(),
                entidade.getCriadoEm());
    }
}
