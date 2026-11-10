package com.tassisfernando.chamegoapi.infrastructure.persistence.pedido;

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
            """)
    List<PedidoJpaEntity> listarPorPeriodo(LocalDate inicio, LocalDate fim);

    List<PedidoJpaEntity> findByClienteId(UUID clienteId);
}
