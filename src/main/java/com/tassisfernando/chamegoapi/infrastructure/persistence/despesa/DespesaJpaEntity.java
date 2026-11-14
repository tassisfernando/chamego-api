package com.tassisfernando.chamegoapi.infrastructure.persistence.despesa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "despesa")
public class DespesaJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String descricao;

    @Column(name = "data_despesa", nullable = false)
    private LocalDate dataDespesa;

    @Column(nullable = false)
    private BigDecimal valor;

    private BigDecimal quantidade;

    @Column(name = "unidade_medida")
    private String unidadeMedida;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "insumo_id")
    private UUID insumoId;

    @Column(name = "ferramenta_id")
    private UUID ferramentaId;

    private String observacao;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataDespesa() {
        return dataDespesa;
    }

    public void setDataDespesa(LocalDate dataDespesa) {
        this.dataDespesa = dataDespesa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(UUID insumoId) {
        this.insumoId = insumoId;
    }

    public UUID getFerramentaId() {
        return ferramentaId;
    }

    public void setFerramentaId(UUID ferramentaId) {
        this.ferramentaId = ferramentaId;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
