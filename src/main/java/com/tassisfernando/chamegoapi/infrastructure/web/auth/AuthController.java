package com.tassisfernando.chamegoapi.infrastructure.web.auth;

import com.tassisfernando.chamegoapi.application.auth.AutenticarUsuarioComando;
import com.tassisfernando.chamegoapi.application.auth.AutenticarUsuarioUseCase;
import com.tassisfernando.chamegoapi.application.auth.TokenAutenticacao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
    }

    @PostMapping("/login")
    public LoginResposta login(@Valid @RequestBody LoginRequisicao requisicao) {
        TokenAutenticacao token = autenticarUsuarioUseCase.executar(
                new AutenticarUsuarioComando(requisicao.usuario(), requisicao.senha()));
        return new LoginResposta(
                token.token(),
                token.tipo(),
                token.expiraEm(),
                token.usuarioId(),
                token.login());
    }

    public record LoginRequisicao(
            @NotBlank String usuario,
            @NotBlank String senha) {
    }

    public record LoginResposta(
            String token,
            String tipo,
            Instant expiraEm,
            UUID usuarioId,
            String login) {
    }
}
