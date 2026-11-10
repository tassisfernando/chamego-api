package com.tassisfernando.chamegoapi.domain.producao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class LoteProducao {

    private final UUID id;
    private final UUID produtoId;
    private final LocalDate dataProducao;
    private final int quantidadeProduzida;
    private final List<ItemConsumoProducao> consumos;
    private final String observacao;

    private LoteProducao(
            UUID id,
            UUID produtoId,
            LocalDate dataProducao,
            int quantidadeProduzida,
            List<ItemConsumoProducao> consumos,
            String observacao) {
        if (produtoId == null) {
            throw new RegraDeNegocioViolada("produto do lote é obrigatório");
        }
        if (quantidadeProduzida <= 0) {
            throw new RegraDeNegocioViolada("quantidade produzida deve ser positiva");
        }
        this.id = id;
        this.produtoId = produtoId;
        this.dataProducao = dataProducao == null ? LocalDate.now() : dataProducao;
        this.quantidadeProduzida = quantidadeProduzida;
        this.consumos = List.copyOf(consumos);
        this.observacao = observacao;
    }

    public static LoteProducao novo(
            UUID produtoId,
            LocalDate dataProducao,
            int quantidadeProduzida,
            List<ItemConsumoProducao> consumos,
            String observacao) {
        return new LoteProducao(
                UUID.randomUUID(), produtoId, dataProducao, quantidadeProduzida, consumos, observacao);
    }

    public static LoteProducao reconstituir(
            UUID id,
            UUID produtoId,
            LocalDate dataProducao,
            int quantidadeProduzida,
            List<ItemConsumoProducao> consumos,
            String observacao) {
        return new LoteProducao(id, produtoId, dataProducao, quantidadeProduzida, consumos, observacao);
    }

    public Dinheiro custoTotal() {
        return consumos.stream()
                .map(ItemConsumoProducao::custoTotal)
                .reduce(Dinheiro.zero(), Dinheiro::somar);
    }

    public Dinheiro custoUnitario() {
        return Dinheiro.de(
                custoTotal()
                        .valor()
                        .divide(BigDecimal.valueOf(quantidadeProduzida), 4, RoundingMode.HALF_UP));
    }

    public UUID id() {
        return id;
    }

    public UUID produtoId() {
        return produtoId;
    }

    public LocalDate dataProducao() {
        return dataProducao;
    }

    public int quantidadeProduzida() {
        return quantidadeProduzida;
    }

    public List<ItemConsumoProducao> consumos() {
        return consumos;
    }

    public String observacao() {
        return observacao;
    }
}
