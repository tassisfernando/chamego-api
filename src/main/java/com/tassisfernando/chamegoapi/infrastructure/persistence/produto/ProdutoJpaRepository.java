package com.tassisfernando.chamegoapi.infrastructure.persistence.produto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoJpaRepository extends JpaRepository<ProdutoJpaEntity, UUID> {

    @Query("select distinct p from ProdutoJpaEntity p left join fetch p.receita where p.id = :id")
    Optional<ProdutoJpaEntity> buscarComReceita(UUID id);

    @Query("select distinct p from ProdutoJpaEntity p left join fetch p.receita where p.ativo = true order by p.nome")
    List<ProdutoJpaEntity> listarAtivosComReceita();

    Page<ProdutoJpaEntity> findByAtivoTrue(Pageable pageable);
}
