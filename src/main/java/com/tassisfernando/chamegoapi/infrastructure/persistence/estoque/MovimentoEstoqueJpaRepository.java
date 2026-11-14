package com.tassisfernando.chamegoapi.infrastructure.persistence.estoque;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MovimentoEstoqueJpaRepository extends JpaRepository<MovimentoEstoqueJpaEntity, UUID> {
}
