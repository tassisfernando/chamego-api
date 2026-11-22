package com.tassisfernando.chamegoapi.infrastructure.web.precificacao;

import com.tassisfernando.chamegoapi.domain.precificacao.ItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoPrecificacao;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PrecificacaoResposta(
        UUID id,
        String titulo,
        TipoPrecificacao tipo,
        UUID produtoReferenciaId,
        UUID produtoPublicadoId,
        BigDecimal margemPercentual,
        BigDecimal precoVenda,
        BigDecimal custoTotal,
        BigDecimal lucroUnitario,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm,
        List<ItemResposta> itens) {

    static PrecificacaoResposta de(Precificacao p, boolean incluirItens) {
        return new PrecificacaoResposta(
                p.id(),
                p.titulo(),
                p.tipo(),
                p.produtoReferenciaId(),
                p.produtoPublicadoId(),
                p.margemPercentual(),
                p.precoVenda().valor(),
                p.custoTotal().valor(),
                p.lucroUnitario().valor(),
                p.observacao(),
                p.criadoEm(),
                p.atualizadoEm(),
                incluirItens
                        ? p.itens().stream().map(ItemResposta::de).toList()
                        : List.of());
    }
}

record ItemResposta(
        UUID id,
        String tipo,
        UUID produtoId,
        UUID insumoId,
        String descricao,
        String modoCalculo,
        BigDecimal quantidade,
        String unidadeMedida,
        BigDecimal custoUnitario,
        BigDecimal valorEmbalagem,
        BigDecimal quantidadeEmbalagem,
        String unidadeEmbalagem,
        BigDecimal subtotal) {

    static ItemResposta de(ItemPrecificacao item) {
        return new ItemResposta(
                item.id(),
                item.tipo().name(),
                item.produtoId(),
                item.insumoId(),
                item.descricao(),
                item.modoCalculo() == null ? null : item.modoCalculo().name(),
                item.quantidade(),
                item.unidadeMedida() == null ? null : item.unidadeMedida().name(),
                item.custoUnitario().valor(),
                item.valorEmbalagem() == null ? null : item.valorEmbalagem().valor(),
                item.quantidadeEmbalagem(),
                item.unidadeEmbalagem() == null ? null : item.unidadeEmbalagem().name(),
                item.subtotal().valor());
    }
}