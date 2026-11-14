package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;

import java.math.BigDecimal;
import java.util.UUID;

public record AtualizarProdutoComando(
        UUID id,
        String nome,
        String descricao,
        CategoriaProduto categoria,
        Integer pesoGramas,
        BigDecimal precoVenda,
        BigDecimal custoPadrao,
        Integer estoqueAtual) {
}
