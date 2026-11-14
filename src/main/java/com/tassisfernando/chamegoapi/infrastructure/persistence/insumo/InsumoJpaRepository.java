package com.tassisfernando.chamegoapi.infrastructure.persistence.insumo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface InsumoJpaRepository extends JpaRepository<InsumoJpaEntity, UUID> {

    List<InsumoJpaEntity> findByAtivoTrue();

    Page<InsumoJpaEntity> findByAtivoTrue(Pageable pageable);

    @Query(
            """
            select i from InsumoJpaEntity i
            where i.ativo = true
              and (
                lower(i.nome) like lower(concat('%', :busca, '%'))
                or lower(i.categoria) like lower(concat('%', :busca, '%'))
                or lower(coalesce(i.marca, '')) like lower(concat('%', :busca, '%'))
                or lower(i.unidadeMedida) like lower(concat('%', :busca, '%'))
              )
            """)
    Page<InsumoJpaEntity> buscarAtivos(@Param("busca") String busca, Pageable pageable);
}
