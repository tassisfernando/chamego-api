package com.tassisfernando.chamegoapi.application.auth;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.usuario.Usuario;
import com.tassisfernando.chamegoapi.domain.usuario.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeradorTokenPort geradorToken;

    public AutenticarUsuarioUseCase(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            GeradorTokenPort geradorToken) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.geradorToken = geradorToken;
    }

    @Transactional(readOnly = true)
    public TokenAutenticacao executar(AutenticarUsuarioComando comando) {
        if (comando.login() == null || comando.login().isBlank()
                || comando.senha() == null || comando.senha().isBlank()) {
            throw new RegraDeNegocioViolada("informe usuário e senha");
        }

        Usuario usuario = usuarioRepository
                .buscarPorLogin(comando.login().trim())
                .orElseThrow(() -> new RegraDeNegocioViolada("usuário ou senha inválidos"));

        if (!usuario.ativo()) {
            throw new RegraDeNegocioViolada("usuário desativado");
        }

        if (!passwordEncoder.matches(comando.senha(), usuario.senhaHash())) {
            throw new RegraDeNegocioViolada("usuário ou senha inválidos");
        }

        String token = geradorToken.gerar(usuario);
        return new TokenAutenticacao(
                token,
                "Bearer",
                geradorToken.expiracaoDe(token),
                usuario.id(),
                usuario.login());
    }
}
