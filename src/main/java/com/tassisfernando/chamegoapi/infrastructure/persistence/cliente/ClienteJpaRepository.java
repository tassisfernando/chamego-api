package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    @Query(
            """
            select c from ClienteJpaEntity c
            where c.dataNascimento is not null
              and month(c.dataNascimento) = :mes
              and day(c.dataNascimento) = :dia
            """)
    List<ClienteJpaEntity> buscarAniversariantes(@Param("mes") int mes, @Param("dia") int dia);
}
