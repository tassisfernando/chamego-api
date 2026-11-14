package com.tassisfernando.chamegoapi.infrastructure.web.cliente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CadastrarClienteRequisicao(
        @NotBlank String nome,
        @Size(max = 20) String telefone,
        String cidade,
        @Size(max = 2) String estado,
        LocalDate dataNascimento) {
}
