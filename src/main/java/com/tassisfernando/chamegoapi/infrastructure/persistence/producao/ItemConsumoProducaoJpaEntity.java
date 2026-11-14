package com.tassisfernando.chamegoapi.infrastructure.persistence.producao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "item_consumo_producao")
public class ItemConsumoProducaoJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_producao_id", nullable = false)
    private LoteProducaoJpaEntity lote;

    @Column(name = "insumo_id", nullable = false)
    private UUID insumoId;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(name = "unidade_medida", nullable = false)
    private String unidadeMedida;

    @Column(name = "custo_unitario", nullable = false)
    private BigDecimal custoUnitario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LoteProducaoJpaEntity getLote() {
        return lote;
    }

    public void setLote(LoteProducaoJpaEntity lote) {
        this.lote = lote;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(UUID insumoId) {
        this.insumoId = insumoId;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }
}
