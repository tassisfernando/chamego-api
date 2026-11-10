package com.tassisfernando.chamegoapi.infrastructure.persistence.estoque;

import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimentoEstoqueRepositoryAdapterTest {

    @Mock
    private MovimentoEstoqueJpaRepository jpaRepository;

    @InjectMocks
    private MovimentoEstoqueRepositoryAdapter adapter;

    @Test
    void deveMapearSaidaDeProdutoParaEntidadeJpa() {
        when(jpaRepository.save(any(MovimentoEstoqueJpaEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID produtoId = UUID.randomUUID();
        MovimentoEstoque movimento = MovimentoEstoque.saidaProduto(
                produtoId,
                Quantidade.unidades(2),
                OrigemMovimento.VENDA,
                UUID.randomUUID(),
                LocalDate.of(2026, 3, 5));

        adapter.salvar(movimento);

        ArgumentCaptor<MovimentoEstoqueJpaEntity> captor = ArgumentCaptor.forClass(MovimentoEstoqueJpaEntity.class);
        verify(jpaRepository).save(captor.capture());
        assertEquals("SAIDA", captor.getValue().getTipo());
        assertEquals("PRODUTO", captor.getValue().getAlvo());
        assertEquals(produtoId, captor.getValue().getProdutoId());
        assertEquals("VENDA", captor.getValue().getOrigem());
    }
}
