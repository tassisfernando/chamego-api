package com.tassisfernando.chamegoapi.application.cliente;

import java.time.LocalDate;

public record CadastrarClienteComando(
        String nome,
        String telefone,
        String cidade,
        String estado,
        LocalDate dataNascimento) {
}
