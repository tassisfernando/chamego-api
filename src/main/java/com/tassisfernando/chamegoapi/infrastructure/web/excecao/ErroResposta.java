package com.tassisfernando.chamegoapi.infrastructure.web.excecao;

import java.time.Instant;

public record ErroResposta(Instant instante, int status, String mensagem) {
}
