package com.tassisfernando.chamegoapi.domain.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.junit.jupiter.api.Test;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.velaComEstoque;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {

    @Test
    void deveBaixarEstoqueAoRegistrarSaida() {
        Produto produto = velaComEstoque(10);

        produto.sairEstoque(3);

        assertEquals(7, produto.estoqueAtual());
    }

    @Test
    void deveRecusarSaidaQuandoEstoqueDoProdutoForInsuficiente() {
        Produto produto = velaComEstoque(2);

        assertThrows(RegraDeNegocioViolada.class, () -> produto.sairEstoque(3));
    }
}
