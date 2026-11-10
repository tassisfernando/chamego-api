package com.tassisfernando.chamegoapi.infrastructure.persistence.producao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lote_producao")
public class LoteProducaoJpaEntity {

    @Id
    private UUID id;

    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    @Column(name = "data_producao", nullable = false)
    private LocalDate dataProducao;

    @Column(name = "quantidade_produzida", nullable = false)
    private int quantidadeProduzida;

    @Column(name = "custo_unitario", nullable = false)
    private BigDecimal custoUnitario;

    @Column(name = "custo_total", nullable = false)
    private BigDecimal custoTotal;

    private String observacao;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemConsumoProducaoJpaEntity> consumos = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public LocalDate getDataProducao() {
        return dataProducao;
    }

    public void setDataProducao(LocalDate dataProducao) {
        this.dataProducao = dataProducao;
    }

    public int getQuantidadeProduzida() {
        return quantidadeProduzida;
    }

    public void setQuantidadeProduzida(int quantidadeProduzida) {
        this.quantidadeProduzida = quantidadeProduzida;
    }

    public BigDecimal getCustoUnitario() {
        return custoUnitario;
    }

    public void setCustoUnitario(BigDecimal custoUnitario) {
        this.custoUnitario = custoUnitario;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<ItemConsumoProducaoJpaEntity> getConsumos() {
        return consumos;
    }
}
