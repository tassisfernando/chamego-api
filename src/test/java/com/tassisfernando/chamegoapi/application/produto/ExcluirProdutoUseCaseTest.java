package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeEmUso;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirProdutoUseCaseTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ExcluirProdutoUseCase useCase;

    @Test
    void deveBloquearExclusaoQuandoProdutoEmUso() {
        var produto = velaComEstoque(1);
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(produtoRepository.possuiReferencias(produto.id())).thenReturn(true);

        assertThrows(EntidadeEmUso.class, () -> useCase.executar(produto.id()));
        verify(produtoRepository, never()).excluir(produto.id());
    }

    @Test
    void deveExcluirQuandoSemReferencias() {
        var produto = velaComEstoque(1);
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(produtoRepository.possuiReferencias(produto.id())).thenReturn(false);

        useCase.executar(produto.id());
        verify(produtoRepository).excluir(produto.id());
    }
}