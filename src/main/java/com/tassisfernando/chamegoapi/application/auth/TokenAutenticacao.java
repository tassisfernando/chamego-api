package com.tassisfernando.chamegoapi.application.auth;

import java.time.Instant;
import java.util.UUID;

public record TokenAutenticacao(
        String token,
        String tipo,
        Instant expiraEm,
        UUID usuarioId,
        String login) {
}
