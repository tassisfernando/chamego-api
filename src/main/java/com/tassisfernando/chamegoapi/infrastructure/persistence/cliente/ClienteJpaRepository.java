package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, UUID> {

    List<ClienteJpaEntity> findByAtivoTrueOrderByNomeAsc();

    Page<ClienteJpaEntity> findByAtivoTrue(Pageable pageable);

    List<ClienteJpaEntity> findByAtivoTrueAndDataNascimentoIsNotNull();

    @Query(
            """
            select c from ClienteJpaEntity c
            where c.ativo = true
              and (
                lower(c.nome) like lower(concat('%', :busca, '%'))
                or lower(coalesce(c.telefone, '')) like lower(concat('%', :busca, '%'))
                or replace(replace(replace(replace(coalesce(c.telefone, ''), ' ', ''), '-', ''), '(', ''), ')', '')
                    like concat('%', :telefoneDigitos, '%')
              )
            """)
    Page<ClienteJpaEntity> buscarAtivos(
            @Param("busca") String busca,
            @Param("telefoneDigitos") String telefoneDigitos,
            Pageable pageable);

    @Query(
            """
            select c from ClienteJpaEntity c
            where c.ativo = true
              and c.dataNascimento is not null
              and month(c.dataNascimento) = :mes
              and day(c.dataNascimento) = :dia
            """)
    List<ClienteJpaEntity> buscarAniversariantes(@Param("mes") int mes, @Param("dia") int dia);
}
