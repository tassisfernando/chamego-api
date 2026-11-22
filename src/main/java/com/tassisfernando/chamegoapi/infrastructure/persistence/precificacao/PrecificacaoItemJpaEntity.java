package com.tassisfernando.chamegoapi.infrastructure.persistence.precificacao;

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
@Table(name = "precificacao_item")
public class PrecificacaoItemJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "precificacao_id", nullable = false)
    private PrecificacaoJpaEntity precificacao;

    @Column(name = "tipo_item", nullable = false)
    private String tipoItem;

    @Column(name = "produto_id")
    private UUID produtoId;

    @Column(name = "insumo_id")
    private UUID insumoId;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal quantidade;

    @Column(name = "unidade_medida")
    private String unidadeMedida;

    @Column(name = "custo_unitario", nullable = false)
    private BigDecimal custoUnitario;

    @Column(name = "modo_calculo", nullable = false)
    private String modoCalculo = "UNITARIO";

    @Column(name = "valor_embalagem")
    private BigDecimal valorEmbalagem;

    @Column(name = "quantidade_embalagem")
    private BigDecimal quantidadeEmbalagem;

    @Column(name = "unidade_embalagem")
    private String unidadeEmbalagem;

    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PrecificacaoJpaEntity getPrecificacao() {
        return precificacao;
    }

    public void setPrecificacao(PrecificacaoJpaEntity precificacao) {
        this.precificacao = precificacao;
    }

    public String getTipoItem() {
        return tipoItem;
    }

    public void setTipoItem(String tipoItem) {
        this.tipoItem = tipoItem;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public UUID getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(UUID insumoId) {
        this.insumoId = insumoId;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getModoCalculo() {
        return modoCalculo;
    }

    public void setModoCalculo(String modoCalculo) {
        this.modoCalculo = modoCalculo;
    }

    public BigDecimal getValorEmbalagem() {
        return valorEmbalagem;
    }

    public void setValorEmbalagem(BigDecimal valorEmbalagem) {
        this.valorEmbalagem = valorEmbalagem;
    }

    public BigDecimal getQuantidadeEmbalagem() {
        return quantidadeEmbalagem;
    }

    public void setQuantidadeEmbalagem(BigDecimal quantidadeEmbalagem) {
        this.quantidadeEmbalagem = quantidadeEmbalagem;
    }

    public String getUnidadeEmbalagem() {
        return unidadeEmbalagem;
    }

    public void setUnidadeEmbalagem(String unidadeEmbalagem) {
        this.unidadeEmbalagem = unidadeEmbalagem;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}