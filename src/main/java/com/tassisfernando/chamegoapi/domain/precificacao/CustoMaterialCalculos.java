package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CustoMaterialCalculos {

    private CustoMaterialCalculos() {}

    public enum FamiliaUnidade {
        MASSA,
        VOLUME,
        COMPRIMENTO,
        UNIDADE
    }

    public static FamiliaUnidade familia(UnidadeMedida unidade) {
        return switch (unidade) {
            case G, KG -> FamiliaUnidade.MASSA;
            case ML, L -> FamiliaUnidade.VOLUME;
            case MT, CM -> FamiliaUnidade.COMPRIMENTO;
            case UN -> FamiliaUnidade.UNIDADE;
        };
    }

    public static BigDecimal normalizarParaBase(UnidadeMedida unidade, BigDecimal quantidade) {
        if (quantidade == null) {
            throw new RegraDeNegocioViolada("quantidade é obrigatória");
        }
        return switch (unidade) {
            case KG -> quantidade.multiply(new BigDecimal("1000"));
            case G -> quantidade;
            case L -> quantidade.multiply(new BigDecimal("1000"));
            case ML -> quantidade;
            case MT -> quantidade.multiply(new BigDecimal("100"));
            case CM -> quantidade;
            case UN -> quantidade;
        };
    }

    public static Dinheiro custoUnitario(Dinheiro precoUnitario, BigDecimal quantidade) {
        if (precoUnitario == null || quantidade == null || quantidade.signum() <= 0) {
            throw new RegraDeNegocioViolada("preço unitário e quantidade devem ser positivos");
        }
        return precoUnitario.multiplicar(quantidade);
    }

    public static Dinheiro custoFracionado(
            Dinheiro valorEmbalagem,
            BigDecimal quantidadeEmbalagem,
            UnidadeMedida unidadeEmbalagem,
            BigDecimal quantidadeUsada,
            UnidadeMedida unidadeUsada) {
        if (valorEmbalagem == null || quantidadeEmbalagem == null || quantidadeUsada == null) {
            throw new RegraDeNegocioViolada("dados da embalagem e quantidade usada são obrigatórios");
        }
        if (valorEmbalagem.valor().signum() == 0) {
            return Dinheiro.zero();
        }
        if (quantidadeEmbalagem.signum() <= 0 || quantidadeUsada.signum() <= 0) {
            throw new RegraDeNegocioViolada("quantidades da embalagem e do uso devem ser positivas");
        }
        if (unidadeEmbalagem == null || unidadeUsada == null) {
            throw new RegraDeNegocioViolada("unidades de medida são obrigatórias no modo fracionado");
        }
        FamiliaUnidade familiaEmb = familia(unidadeEmbalagem);
        FamiliaUnidade familiaUso = familia(unidadeUsada);
        if (familiaEmb != familiaUso) {
            throw new RegraDeNegocioViolada(
                    "unidades incompatíveis: não é possível converter "
                            + familiaEmb.name().toLowerCase()
                            + " para "
                            + familiaUso.name().toLowerCase());
        }
        if (familiaEmb == FamiliaUnidade.UNIDADE && unidadeEmbalagem != unidadeUsada) {
            throw new RegraDeNegocioViolada("no modo por unidade, embalagem e uso devem usar a mesma medida (un)");
        }

        BigDecimal embalagemNormalizada = normalizarParaBase(unidadeEmbalagem, quantidadeEmbalagem);
        BigDecimal usoNormalizado = normalizarParaBase(unidadeUsada, quantidadeUsada);

        BigDecimal custo = valorEmbalagem
                .valor()
                .divide(embalagemNormalizada, 10, RoundingMode.HALF_UP)
                .multiply(usoNormalizado)
                .setScale(2, RoundingMode.HALF_UP);

        return Dinheiro.de(custo);
    }
}