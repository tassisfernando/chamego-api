package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class PrecificacaoCalculos {

    private static final int ESCALA_INTERNA = 6;

    private PrecificacaoCalculos() {}

    public static ResultadoMargemPreco calcularPorMargem(Dinheiro custoTotal, BigDecimal margemPercentual) {
        BigDecimal margem = normalizarPercentual(margemPercentual);
        BigDecimal fator = BigDecimal.ONE.add(
                margem.divide(BigDecimal.valueOf(100), ESCALA_INTERNA, RoundingMode.HALF_UP));
        Dinheiro preco = Dinheiro.de(custoTotal.valor().multiply(fator));
        Dinheiro lucro = preco.subtrair(custoTotal);
        return new ResultadoMargemPreco(custoTotal, margem, preco, lucro, alertaBaixaMargem(margem));
    }

    public static ResultadoMargemPreco calcularPorPreco(Dinheiro custoTotal, Dinheiro precoVenda) {
        if (precoVenda.valor().signum() < 0) {
            throw new RegraDeNegocioViolada("preço de venda não pode ser negativo");
        }
        if (custoTotal.valor().signum() == 0) {
            return new ResultadoMargemPreco(
                    custoTotal, BigDecimal.ZERO, precoVenda, precoVenda, alertaCustoZero(precoVenda));
        }
        BigDecimal margem = precoVenda
                .subtrair(custoTotal)
                .valor()
                .divide(custoTotal.valor(), ESCALA_INTERNA, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        Dinheiro lucro = precoVenda.subtrair(custoTotal);
        return new ResultadoMargemPreco(custoTotal, margem, precoVenda, lucro, alertaBaixaMargem(margem));
    }

    public static Dinheiro somarLinhas(java.util.List<Dinheiro> subtotais) {
        return subtotais.stream().reduce(Dinheiro.zero(), Dinheiro::somar);
    }

    private static BigDecimal normalizarPercentual(BigDecimal margem) {
        if (margem == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return margem.setScale(2, RoundingMode.HALF_UP);
    }

    private static String alertaBaixaMargem(BigDecimal margem) {
        if (margem.compareTo(BigDecimal.ZERO) < 0) {
            return "MARGEM_NEGATIVA";
        }
        if (margem.compareTo(new BigDecimal("30")) < 0) {
            return "MARGEM_BAIXA";
        }
        return null;
    }

    private static String alertaCustoZero(Dinheiro preco) {
        if (preco.valor().signum() <= 0) {
            return "CUSTO_ZERO";
        }
        return "CUSTO_ZERO";
    }

    public record ResultadoMargemPreco(
            Dinheiro custoTotal,
            BigDecimal margemPercentual,
            Dinheiro precoVenda,
            Dinheiro lucroUnitario,
            String alerta) {}
}