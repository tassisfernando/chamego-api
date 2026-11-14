package com.tassisfernando.chamegoapi.infrastructure.persistence.despesa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaJpaRepository extends JpaRepository<DespesaJpaEntity, UUID> {

    List<DespesaJpaEntity> findByDataDespesaBetween(LocalDate inicio, LocalDate fim);

    Page<DespesaJpaEntity> findByDataDespesaBetween(LocalDate inicio, LocalDate fim, Pageable pageable);
}
