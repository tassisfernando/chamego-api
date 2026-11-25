package com.tassisfernando.chamegoapi.infrastructure.persistence.pedido;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoJpaRepository extends JpaRepository<PedidoJpaEntity, UUID> {

    @Query("select distinct p from PedidoJpaEntity p left join fetch p.itens where p.id = :id")
    Optional<PedidoJpaEntity> buscarComItens(UUID id);

    @Query(
            """
            select distinct p from PedidoJpaEntity p left join fetch p.itens
            where p.dataPedido between :inicio and :fim
            order by p.dataPedido desc
            """)
    List<PedidoJpaEntity> listarPorPeriodo(LocalDate inicio, LocalDate fim);

    @Query(
            value = "select p from PedidoJpaEntity p where p.dataPedido between :inicio and :fim",
            countQuery = "select count(p) from PedidoJpaEntity p where p.dataPedido between :inicio and :fim")
    Page<PedidoJpaEntity> listarPorPeriodo(LocalDate inicio, LocalDate fim, Pageable pageable);

    @Query(
            value =
                    """
                    select p from PedidoJpaEntity p
                    where p.dataPedido between :inicio and :fim
                      and (:status is null or p.status = :status)
                    """,
            countQuery =
                    """
                    select count(p) from PedidoJpaEntity p
                    where p.dataPedido between :inicio and :fim
                      and (:status is null or p.status = :status)
                    """)
    Page<PedidoJpaEntity> listarPorPeriodo(
            LocalDate inicio, LocalDate fim, String status, Pageable pageable);

    List<PedidoJpaEntity> findByClienteId(UUID clienteId);

    @Query(
            """
            select distinct p from PedidoJpaEntity p left join fetch p.itens
            where p.dataEntrega is not null
              and p.dataEntrega between :inicio and :fim
              and p.status <> 'CANCELADO'
            order by p.dataEntrega asc
            """)
    List<PedidoJpaEntity> listarPorDataEntrega(LocalDate inicio, LocalDate fim);
}
