package com.tassisfernando.chamegoapi.infrastructure.persistence.ferramenta;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FerramentaJpaRepository extends JpaRepository<FerramentaJpaEntity, UUID> {

    List<FerramentaJpaEntity> findByAtivoTrue();
}
