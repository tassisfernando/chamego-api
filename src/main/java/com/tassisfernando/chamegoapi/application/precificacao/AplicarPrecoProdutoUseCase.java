package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AplicarPrecoProdutoUseCase {

    private final PrecificacaoRepository precificacaoRepository;
    private final ProdutoRepository produtoRepository;

    public AplicarPrecoProdutoUseCase(PrecificacaoRepository precificacaoRepository, ProdutoRepository produtoRepository) {
        this.precificacaoRepository = precificacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto executar(UUID precificacaoId, UUID produtoId) {
        Precificacao precificacao = precificacaoRepository
                .buscarPorId(precificacaoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Precificação", precificacaoId));
        UUID destinoId =
                produtoId != null ? produtoId : precificacao.produtoReferenciaId();
        if (destinoId == null) {
            throw new RegraDeNegocioViolada(
                    "informe o produto ou salve a precificação vinculada a uma vela do catálogo");
        }
        Produto produto = produtoRepository
                .buscarPorId(destinoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", destinoId));
        produto.atualizarDados(
                produto.nome(),
                produto.descricao(),
                produto.categoria(),
                produto.pesoGramas(),
                precificacao.precoVenda(),
                precificacao.custoTotal());
        return produtoRepository.salvar(produto);
    }
}