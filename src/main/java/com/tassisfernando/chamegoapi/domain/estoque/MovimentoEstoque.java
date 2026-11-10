package com.tassisfernando.chamegoapi.domain.estoque;

import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.time.LocalDate;
import java.util.UUID;

public record MovimentoEstoque(
        UUID id,
        TipoMovimentoEstoque tipo,
        AlvoEstoque alvo,
        UUID insumoId,
        UUID produtoId,
        Quantidade quantidade,
        OrigemMovimento origem,
        UUID referenciaId,
        LocalDate dataMovimento) {

    public MovimentoEstoque {
        if (alvo == AlvoEstoque.INSUMO && insumoId == null) {
            throw new RegraDeNegocioViolada("movimento de insumo exige insumoId");
        }
        if (alvo == AlvoEstoque.PRODUTO && produtoId == null) {
            throw new RegraDeNegocioViolada("movimento de produto exige produtoId");
        }
    }

    public static MovimentoEstoque entradaInsumo(
            UUID insumoId, Quantidade quantidade, OrigemMovimento origem, UUID referenciaId, LocalDate data) {
        return new MovimentoEstoque(
                UUID.randomUUID(),
                TipoMovimentoEstoque.ENTRADA,
                AlvoEstoque.INSUMO,
                insumoId,
                null,
                quantidade,
                origem,
                referenciaId,
                data);
    }

    public static MovimentoEstoque saidaInsumo(
            UUID insumoId, Quantidade quantidade, OrigemMovimento origem, UUID referenciaId, LocalDate data) {
        return new MovimentoEstoque(
                UUID.randomUUID(),
                TipoMovimentoEstoque.SAIDA,
                AlvoEstoque.INSUMO,
                insumoId,
                null,
                quantidade,
                origem,
                referenciaId,
                data);
    }

    public static MovimentoEstoque entradaProduto(
            UUID produtoId, Quantidade quantidade, OrigemMovimento origem, UUID referenciaId, LocalDate data) {
        return new MovimentoEstoque(
                UUID.randomUUID(),
                TipoMovimentoEstoque.ENTRADA,
                AlvoEstoque.PRODUTO,
                null,
                produtoId,
                quantidade,
                origem,
                referenciaId,
                data);
    }

    public static MovimentoEstoque saidaProduto(
            UUID produtoId, Quantidade quantidade, OrigemMovimento origem, UUID referenciaId, LocalDate data) {
        return new MovimentoEstoque(
                UUID.randomUUID(),
                TipoMovimentoEstoque.SAIDA,
                AlvoEstoque.PRODUTO,
                null,
                produtoId,
                quantidade,
                origem,
                referenciaId,
                data);
    }
}
