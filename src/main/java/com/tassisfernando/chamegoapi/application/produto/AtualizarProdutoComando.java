package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record AtualizarProdutoComando(
        UUID id,
        String nome,
        String descricao,
        CategoriaProduto categoria,
        Integer pesoGramas,
        BigDecimal precoVenda,
        BigDecimal custoPadrao,
        Integer estoqueAtual,
        List<ItemReceita> receita) {}