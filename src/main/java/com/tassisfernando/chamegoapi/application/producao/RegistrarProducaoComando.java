package com.tassisfernando.chamegoapi.application.producao;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrarProducaoComando(
        UUID produtoId, LocalDate dataProducao, int quantidadeProduzida, String observacao) {
}
