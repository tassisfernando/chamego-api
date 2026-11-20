package com.tassisfernando.chamegoapi.application.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeEmUso;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ExcluirProdutoUseCase {

    private final ProdutoRepository produtoRepository;

    public ExcluirProdutoUseCase(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public void executar(UUID produtoId) {
        produtoRepository
                .buscarPorId(produtoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", produtoId));
        if (produtoRepository.possuiReferencias(produtoId)) {
            throw new EntidadeEmUso(
                    "Não é possível excluir este produto porque ele aparece em pedidos ou registros de produção.");
        }
        produtoRepository.excluir(produtoId);
    }
}