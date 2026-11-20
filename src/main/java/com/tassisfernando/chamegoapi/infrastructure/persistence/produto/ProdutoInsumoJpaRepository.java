package com.tassisfernando.chamegoapi.infrastructure.persistence.produto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProdutoInsumoJpaRepository extends JpaRepository<ProdutoInsumoJpaEntity, UUID> {

    boolean existsByInsumoId(UUID insumoId);
}