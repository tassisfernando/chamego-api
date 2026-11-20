package com.tassisfernando.chamegoapi.infrastructure.web.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.StatusCupom;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteResposta(
        UUID id,
        String nome,
        String telefone,
        String email,
        String cidade,
        String estado,
        LocalDate dataCadastro,
        LocalDate dataNascimento,
        StatusCupom statusCupom,
        String observacao,
        boolean ativo) {
}
