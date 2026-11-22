package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtualizarProdutoUseCaseTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private AtualizarProdutoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarProdutoUseCase(produtoRepository, movimentoEstoqueRepository);
    }

    @Test
    void atualizaReceitaDoProduto() {
        Produto produto = velaComEstoque(3);
        when(produtoRepository.buscarPorId(produto.id())).thenReturn(Optional.of(produto));
        when(produtoRepository.salvar(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        var cera = ceraComEstoque("1000", "0.05");
        var receita = List.of(new ItemReceita(cera.id(), quantidade("120", UnidadeMedida.G)));

        useCase.executar(new AtualizarProdutoComando(
                produto.id(),
                produto.nome(),
                null,
                CategoriaProduto.VELA,
                125,
                produto.precoVenda().valor(),
                produto.custoPadrao().valor(),
                null,
                receita));

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).salvar(captor.capture());
        assertEquals(1, captor.getValue().receita().size());
        assertEquals(0, new BigDecimal("120").compareTo(captor.getValue().receita().getFirst().quantidadePorUnidade().valor()));
    }
}