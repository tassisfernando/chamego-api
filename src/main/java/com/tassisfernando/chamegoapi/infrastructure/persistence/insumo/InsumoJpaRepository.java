package com.tassisfernando.chamegoapi.infrastructure.persistence.insumo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InsumoJpaRepository extends JpaRepository<InsumoJpaEntity, UUID> {

    List<InsumoJpaEntity> findByAtivoTrue();

    Page<InsumoJpaEntity> findByAtivoTrue(Pageable pageable);
}
