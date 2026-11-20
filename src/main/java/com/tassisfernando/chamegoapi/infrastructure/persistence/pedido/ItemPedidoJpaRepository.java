package com.tassisfernando.chamegoapi.infrastructure.persistence.pedido;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemPedidoJpaRepository extends JpaRepository<ItemPedidoJpaEntity, UUID> {

    boolean existsByProdutoId(UUID produtoId);
}