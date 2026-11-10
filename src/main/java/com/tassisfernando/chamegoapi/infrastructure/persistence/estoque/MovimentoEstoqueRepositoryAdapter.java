package com.tassisfernando.chamegoapi.infrastructure.persistence.estoque;

import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import org.springframework.stereotype.Repository;

@Repository
public class MovimentoEstoqueRepositoryAdapter implements MovimentoEstoqueRepository {

    private final MovimentoEstoqueJpaRepository jpaRepository;

    public MovimentoEstoqueRepositoryAdapter(MovimentoEstoqueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public MovimentoEstoque salvar(MovimentoEstoque movimento) {
        MovimentoEstoqueJpaEntity entidade = new MovimentoEstoqueJpaEntity();
        entidade.setId(movimento.id());
        entidade.setTipo(movimento.tipo().name());
        entidade.setAlvo(movimento.alvo().name());
        entidade.setInsumoId(movimento.insumoId());
        entidade.setProdutoId(movimento.produtoId());
        entidade.setQuantidade(movimento.quantidade().valor());
        entidade.setOrigem(movimento.origem().name());
        entidade.setReferenciaId(movimento.referenciaId());
        entidade.setDataMovimento(movimento.dataMovimento());
        jpaRepository.save(entidade);
        return movimento;
    }
}
