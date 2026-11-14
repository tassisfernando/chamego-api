package com.tassisfernando.chamegoapi.application.cliente;

import java.time.LocalDate;
import java.util.UUID;

public record AtualizarClienteComando(
        UUID id,
        String nome,
        String telefone,
        String cidade,
        String estado,
        LocalDate dataNascimento) {
}
