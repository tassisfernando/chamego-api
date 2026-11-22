package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPrecificacao(
        UUID id,
        TipoItemPrecificacao tipo,
        UUID produtoId,
        UUID insumoId,
        String descricao,
        ModoCalculoMaterial modoCalculo,
        BigDecimal quantidade,
        UnidadeMedida unidadeMedida,
        Dinheiro custoUnitario,
        Dinheiro valorEmbalagem,
        BigDecimal quantidadeEmbalagem,
        UnidadeMedida unidadeEmbalagem) {

    public Dinheiro subtotal() {
        if (tipo == TipoItemPrecificacao.PRODUTO) {
            return CustoMaterialCalculos.custoUnitario(custoUnitario, quantidade);
        }
        if (modoCalculo == ModoCalculoMaterial.FRACIONADO) {
            return CustoMaterialCalculos.custoFracionado(
                    valorEmbalagem, quantidadeEmbalagem, unidadeEmbalagem, quantidade, unidadeMedida);
        }
        return CustoMaterialCalculos.custoUnitario(custoUnitario, quantidade);
    }

    public static ItemPrecificacao deInsumo(
            UUID insumoId,
            String descricao,
            BigDecimal quantidade,
            UnidadeMedida unidade,
            Dinheiro custoUnitario) {
        return material(
                TipoItemPrecificacao.INSUMO,
                insumoId,
                null,
                descricao,
                ModoCalculoMaterial.UNITARIO,
                quantidade,
                unidade,
                custoUnitario,
                null,
                null,
                null);
    }

    public static ItemPrecificacao deProduto(
            UUID produtoId, String descricao, BigDecimal quantidade, Dinheiro custoUnitario) {
        return new ItemPrecificacao(
                UUID.randomUUID(),
                TipoItemPrecificacao.PRODUTO,
                produtoId,
                null,
                descricao,
                ModoCalculoMaterial.UNITARIO,
                quantidade,
                UnidadeMedida.UN,
                custoUnitario,
                null,
                null,
                null);
    }

    public static ItemPrecificacao extra(String descricao, BigDecimal quantidade, Dinheiro custoUnitario) {
        return material(
                TipoItemPrecificacao.EXTRA,
                null,
                null,
                descricao,
                ModoCalculoMaterial.UNITARIO,
                quantidade,
                UnidadeMedida.UN,
                custoUnitario,
                null,
                null,
                null);
    }

    public static ItemPrecificacao material(
            TipoItemPrecificacao tipo,
            UUID insumoId,
            UUID produtoId,
            String descricao,
            ModoCalculoMaterial modoCalculo,
            BigDecimal quantidadeUsada,
            UnidadeMedida unidadeUsada,
            Dinheiro custoUnitario,
            Dinheiro valorEmbalagem,
            BigDecimal quantidadeEmbalagem,
            UnidadeMedida unidadeEmbalagem) {
        return new ItemPrecificacao(
                UUID.randomUUID(),
                tipo,
                produtoId,
                insumoId,
                descricao,
                modoCalculo == null ? ModoCalculoMaterial.UNITARIO : modoCalculo,
                quantidadeUsada,
                unidadeUsada,
                custoUnitario == null ? Dinheiro.zero() : custoUnitario,
                valorEmbalagem,
                quantidadeEmbalagem,
                unidadeEmbalagem);
    }

    public static ItemPrecificacao reconstituir(
            UUID id,
            TipoItemPrecificacao tipo,
            UUID produtoId,
            UUID insumoId,
            String descricao,
            ModoCalculoMaterial modoCalculo,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            Dinheiro custoUnitario,
            Dinheiro valorEmbalagem,
            BigDecimal quantidadeEmbalagem,
            UnidadeMedida unidadeEmbalagem) {
        return new ItemPrecificacao(
                id,
                tipo,
                produtoId,
                insumoId,
                descricao,
                modoCalculo == null ? ModoCalculoMaterial.UNITARIO : modoCalculo,
                quantidade,
                unidadeMedida,
                custoUnitario,
                valorEmbalagem,
                quantidadeEmbalagem,
                unidadeEmbalagem);
    }
}