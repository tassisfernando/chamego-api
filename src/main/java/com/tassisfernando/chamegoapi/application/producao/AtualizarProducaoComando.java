package com.tassisfernando.chamegoapi.application.producao;

import java.time.LocalDate;
import java.util.UUID;

public record AtualizarProducaoComando(
        UUID id, UUID produtoId, LocalDate dataProducao, int quantidadeProduzida, String observacao) {
}
