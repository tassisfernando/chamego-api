package com.tassisfernando.chamegoapi.infrastructure.persistence.producao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemConsumoProducaoJpaRepository extends JpaRepository<ItemConsumoProducaoJpaEntity, UUID> {

    boolean existsByInsumoId(UUID insumoId);
}