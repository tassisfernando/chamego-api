package com.tassisfernando.chamegoapi.infrastructure.persistence.precificacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PrecificacaoJpaRepository extends JpaRepository<PrecificacaoJpaEntity, UUID> {

    @Query("select p from PrecificacaoJpaEntity p left join fetch p.itens where p.id = :id")
    Optional<PrecificacaoJpaEntity> buscarComItens(@Param("id") UUID id);

    Page<PrecificacaoJpaEntity> findAllByOrderByCriadoEmDesc(Pageable pageable);
}