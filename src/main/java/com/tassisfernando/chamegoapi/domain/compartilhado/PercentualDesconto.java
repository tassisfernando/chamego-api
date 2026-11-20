package com.tassisfernando.chamegoapi.domain.compartilhado;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PercentualDesconto(BigDecimal valor) {

  private static final BigDecimal CEM = BigDecimal.valueOf(100);

  public PercentualDesconto {
    if (valor == null) {
      valor = BigDecimal.ZERO;
    }
    valor = valor.setScale(2, RoundingMode.HALF_UP);
    if (valor.compareTo(BigDecimal.ZERO) < 0 || valor.compareTo(CEM) > 0) {
      throw new RegraDeNegocioViolada("desconto percentual deve estar entre 0 e 100");
    }
  }

  public static PercentualDesconto zero() {
    return new PercentualDesconto(BigDecimal.ZERO);
  }

  public static PercentualDesconto de(BigDecimal valor) {
    return valor == null ? zero() : new PercentualDesconto(valor);
  }

  /** Fator multiplicador após desconto (ex.: 10% → 0,90). */
  public BigDecimal fatorLiquido() {
    BigDecimal fracao = valor.divide(CEM, 6, RoundingMode.HALF_UP);
    return BigDecimal.ONE.subtract(fracao);
  }
}