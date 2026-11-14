package com.tassisfernando.chamegoapi.suporte;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import com.tassisfernando.chamegoapi.domain.insumo.CategoriaInsumo;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.produto.Produto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class FixturesDominio {

    private FixturesDominio() {
    }

    public static Dinheiro brl(String valor) {
        return Dinheiro.de(new BigDecimal(valor));
    }

    public static Quantidade quantidade(String valor, UnidadeMedida unidade) {
        return Quantidade.de(new BigDecimal(valor), unidade);
    }

    public static Cliente cliente(String nome) {
        return Cliente.novo(nome, "31999999999", "IPATINGA", "MG", LocalDate.of(1990, 7, 3));
    }

    public static Produto velaComEstoque(int estoque) {
        return Produto.reconstituir(
                UUID.randomUUID(),
                "LAVANDA 100G",
                null,
                CategoriaProduto.VELA,
                100,
                brl("32.90"),
                brl("16.45"),
                estoque,
                true,
                List.of());
    }

    public static Produto velaComReceita(Insumo insumo, Quantidade quantidadePorUnidade) {
        Produto produto = Produto.novo("LAVANDA 100G", CategoriaProduto.VELA, 100, brl("32.90"), brl("16.45"));
        produto.definirReceita(List.of(new ItemReceita(insumo.id(), quantidadePorUnidade)));
        return produto;
    }

    public static Insumo ceraComEstoque(String estoque, String custoMedio) {
        return Insumo.reconstituir(
                UUID.randomUUID(),
                "CERA DE COCO",
                CategoriaInsumo.CERA,
                "SOLVEN",
                UnidadeMedida.G,
                quantidade(estoque, UnidadeMedida.G),
                brl(custoMedio),
                true);
    }

    public static Pedido pedidoConfirmado(NaturezaPedido natureza, ItemPedido item) {
        Pedido pedido = Pedido.novo(UUID.randomUUID(), LocalDate.of(2026, 3, 5), natureza, null);
        pedido.adicionarItem(item);
        pedido.confirmar();
        return pedido;
    }

    public static Despesa despesaOperacional(String valor) {
        return Despesa.nova(
                "HOT PRINT",
                LocalDate.of(2026, 3, 10),
                brl(valor),
                null,
                TipoDespesa.OPERACIONAL,
                null,
                null,
                null);
    }
}
