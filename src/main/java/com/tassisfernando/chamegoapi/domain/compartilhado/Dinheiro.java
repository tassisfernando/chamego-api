package com.tassisfernando.chamegoapi.domain.compartilhado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Dinheiro(BigDecimal valor) {

    public static final int ESCALA = 2;

    public Dinheiro {
        Objects.requireNonNull(valor, "valor monetário é obrigatório");
        valor = valor.setScale(ESCALA, RoundingMode.HALF_UP);
    }

    public static Dinheiro zero() {
        return new Dinheiro(BigDecimal.ZERO);
    }

    public static Dinheiro de(BigDecimal valor) {
        return valor == null ? zero() : new Dinheiro(valor);
    }

    public static Dinheiro de(double valor) {
        return new Dinheiro(BigDecimal.valueOf(valor));
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(valor.add(outro.valor));
    }

    public Dinheiro subtrair(Dinheiro outro) {
        return new Dinheiro(valor.subtract(outro.valor));
    }

    public Dinheiro multiplicar(BigDecimal fator) {
        return new Dinheiro(valor.multiply(fator));
    }

    public boolean maiorQue(Dinheiro outro) {
        return valor.compareTo(outro.valor) > 0;
    }
}
