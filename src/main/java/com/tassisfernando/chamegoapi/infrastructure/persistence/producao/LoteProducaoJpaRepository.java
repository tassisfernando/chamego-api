package com.tassisfernando.chamegoapi.infrastructure.persistence.producao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoteProducaoJpaRepository extends JpaRepository<LoteProducaoJpaEntity, UUID> {

    @Query("select distinct l from LoteProducaoJpaEntity l left join fetch l.consumos where l.id = :id")
    Optional<LoteProducaoJpaEntity> buscarComConsumos(UUID id);

    @Query(
            """
            select distinct l from LoteProducaoJpaEntity l left join fetch l.consumos
            where l.dataProducao between :inicio and :fim
            order by l.dataProducao desc
            """)
    List<LoteProducaoJpaEntity> listarPorPeriodo(LocalDate inicio, LocalDate fim);

    @Query(
            value = "select l from LoteProducaoJpaEntity l where l.dataProducao between :inicio and :fim",
            countQuery =
                    "select count(l) from LoteProducaoJpaEntity l where l.dataProducao between :inicio and :fim")
    Page<LoteProducaoJpaEntity> listarPorPeriodo(LocalDate inicio, LocalDate fim, Pageable pageable);
}
