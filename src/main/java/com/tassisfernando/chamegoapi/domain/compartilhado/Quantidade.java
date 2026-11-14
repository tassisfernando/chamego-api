package com.tassisfernando.chamegoapi.domain.compartilhado;

import java.math.BigDecimal;
import java.util.Objects;

public record Quantidade(BigDecimal valor, UnidadeMedida unidade) {

    public Quantidade {
        Objects.requireNonNull(valor, "quantidade é obrigatória");
        Objects.requireNonNull(unidade, "unidade de medida é obrigatória");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraDeNegocioViolada("quantidade não pode ser negativa");
        }
    }

    public static Quantidade de(BigDecimal valor, UnidadeMedida unidade) {
        return new Quantidade(valor, unidade);
    }

    public static Quantidade unidades(int quantidade) {
        return new Quantidade(BigDecimal.valueOf(quantidade), UnidadeMedida.UN);
    }

    public Quantidade somar(Quantidade outra) {
        garantirMesmaUnidade(outra);
        return new Quantidade(valor.add(outra.valor), unidade);
    }

    public Quantidade subtrair(Quantidade outra) {
        garantirMesmaUnidade(outra);
        if (valor.compareTo(outra.valor) < 0) {
            throw new RegraDeNegocioViolada("estoque insuficiente");
        }
        return new Quantidade(valor.subtract(outra.valor), unidade);
    }

    public boolean menorQue(Quantidade outra) {
        garantirMesmaUnidade(outra);
        return valor.compareTo(outra.valor) < 0;
    }

    private void garantirMesmaUnidade(Quantidade outra) {
        if (unidade != outra.unidade) {
            throw new RegraDeNegocioViolada(
                    "unidades incompatíveis: " + unidade + " e " + outra.unidade);
        }
    }
}
