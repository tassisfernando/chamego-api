package com.tassisfernando.chamegoapi.infrastructure.persistence.precificacao;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "precificacao")
public class PrecificacaoJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String tipo;

    @Column(name = "produto_referencia_id")
    private UUID produtoReferenciaId;

    @Column(name = "produto_publicado_id")
    private UUID produtoPublicadoId;

    @Column(name = "margem_percentual", nullable = false)
    private BigDecimal margemPercentual;

    @Column(name = "preco_venda", nullable = false)
    private BigDecimal precoVenda;

    @Column(name = "custo_total", nullable = false)
    private BigDecimal custoTotal;

    @Column(name = "lucro_unitario", nullable = false)
    private BigDecimal lucroUnitario;

    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    @OneToMany(mappedBy = "precificacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrecificacaoItemJpaEntity> itens = new ArrayList<>();

    @PrePersist
    void aoCriar() {
        Instant agora = Instant.now();
        if (criadoEm == null) {
            criadoEm = agora;
        }
        atualizadoEm = agora;
    }

    @PreUpdate
    void aoAtualizar() {
        atualizadoEm = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public UUID getProdutoReferenciaId() {
        return produtoReferenciaId;
    }

    public void setProdutoReferenciaId(UUID produtoReferenciaId) {
        this.produtoReferenciaId = produtoReferenciaId;
    }

    public UUID getProdutoPublicadoId() {
        return produtoPublicadoId;
    }

    public void setProdutoPublicadoId(UUID produtoPublicadoId) {
        this.produtoPublicadoId = produtoPublicadoId;
    }

    public BigDecimal getMargemPercentual() {
        return margemPercentual;
    }

    public void setMargemPercentual(BigDecimal margemPercentual) {
        this.margemPercentual = margemPercentual;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public BigDecimal getCustoTotal() {
        return custoTotal;
    }

    public void setCustoTotal(BigDecimal custoTotal) {
        this.custoTotal = custoTotal;
    }

    public BigDecimal getLucroUnitario() {
        return lucroUnitario;
    }

    public void setLucroUnitario(BigDecimal lucroUnitario) {
        this.lucroUnitario = lucroUnitario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Instant atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public List<PrecificacaoItemJpaEntity> getItens() {
        return itens;
    }

    public void setItens(List<PrecificacaoItemJpaEntity> itens) {
        this.itens = itens;
    }
}