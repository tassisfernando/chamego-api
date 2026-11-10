package com.tassisfernando.chamegoapi.domain.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;

import java.time.LocalDate;
import java.util.UUID;

public final class Despesa {

    private final UUID id;
    private String descricao;
    private LocalDate dataDespesa;
    private Dinheiro valor;
    private Quantidade quantidade;
    private TipoDespesa tipo;
    private UUID insumoId;
    private UUID ferramentaId;
    private String observacao;

    private Despesa(
            UUID id,
            String descricao,
            LocalDate dataDespesa,
            Dinheiro valor,
            Quantidade quantidade,
            TipoDespesa tipo,
            UUID insumoId,
            UUID ferramentaId,
            String observacao) {
        if (descricao == null || descricao.isBlank()) {
            throw new RegraDeNegocioViolada("descrição da despesa é obrigatória");
        }
        if (tipo == TipoDespesa.INSUMO && insumoId == null) {
            throw new RegraDeNegocioViolada("despesa de insumo precisa do insumo vinculado");
        }
        if (tipo == TipoDespesa.FERRAMENTA && ferramentaId == null) {
            throw new RegraDeNegocioViolada("despesa de ferramenta precisa da ferramenta vinculada");
        }
        this.id = id;
        this.descricao = descricao.trim();
        this.dataDespesa = dataDespesa == null ? LocalDate.now() : dataDespesa;
        this.valor = valor;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.insumoId = insumoId;
        this.ferramentaId = ferramentaId;
        this.observacao = observacao;
    }

    public static Despesa nova(
            String descricao,
            LocalDate dataDespesa,
            Dinheiro valor,
            Quantidade quantidade,
            TipoDespesa tipo,
            UUID insumoId,
            UUID ferramentaId,
            String observacao) {
        return new Despesa(
                UUID.randomUUID(),
                descricao,
                dataDespesa,
                valor,
                quantidade,
                tipo,
                insumoId,
                ferramentaId,
                observacao);
    }

    public static Despesa reconstituir(
            UUID id,
            String descricao,
            LocalDate dataDespesa,
            Dinheiro valor,
            Quantidade quantidade,
            TipoDespesa tipo,
            UUID insumoId,
            UUID ferramentaId,
            String observacao) {
        return new Despesa(
                id, descricao, dataDespesa, valor, quantidade, tipo, insumoId, ferramentaId, observacao);
    }

    public boolean atualizaEstoqueInsumo() {
        return tipo == TipoDespesa.INSUMO && insumoId != null && quantidade != null;
    }

    public Dinheiro custoUnitario() {
        if (quantidade == null || quantidade.valor().signum() == 0) {
            return valor;
        }
        return Dinheiro.de(valor.valor().divide(quantidade.valor(), 4, java.math.RoundingMode.HALF_UP));
    }

    public UUID id() {
        return id;
    }

    public String descricao() {
        return descricao;
    }

    public LocalDate dataDespesa() {
        return dataDespesa;
    }

    public Dinheiro valor() {
        return valor;
    }

    public Quantidade quantidade() {
        return quantidade;
    }

    public TipoDespesa tipo() {
        return tipo;
    }

    public UUID insumoId() {
        return insumoId;
    }

    public UUID ferramentaId() {
        return ferramentaId;
    }

    public String observacao() {
        return observacao;
    }
}
