package com.tassisfernando.chamegoapi.application.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoPrecificacao;
import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PublicarKitComoProdutoUseCase {

    private final PrecificacaoRepository precificacaoRepository;
    private final ProdutoRepository produtoRepository;

    public PublicarKitComoProdutoUseCase(
            PrecificacaoRepository precificacaoRepository, ProdutoRepository produtoRepository) {
        this.precificacaoRepository = precificacaoRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto executar(UUID precificacaoId, String nomeProduto) {
        Precificacao precificacao = precificacaoRepository
                .buscarPorId(precificacaoId)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Precificação", precificacaoId));
        if (precificacao.tipo() != TipoPrecificacao.KIT) {
            throw new RegraDeNegocioViolada("apenas precificações do tipo KIT podem ser publicadas como produto");
        }
        String nome = nomeProduto != null && !nomeProduto.isBlank() ? nomeProduto.trim() : precificacao.titulo();
        Produto produto;
        if (precificacao.produtoPublicadoId() != null) {
            produto = produtoRepository
                    .buscarPorId(precificacao.produtoPublicadoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Produto", precificacao.produtoPublicadoId()));
            produto.atualizarDados(
                    nome,
                    precificacao.observacao(),
                    CategoriaProduto.KIT,
                    null,
                    precificacao.precoVenda(),
                    precificacao.custoTotal());
        } else {
            produto = Produto.novo(
                    nome,
                    CategoriaProduto.KIT,
                    null,
                    precificacao.precoVenda(),
                    precificacao.custoTotal());
        }
        produto = produtoRepository.salvar(produto);
        precificacao.vincularProdutoPublicado(produto.id());
        precificacaoRepository.salvar(precificacao);
        return produto;
    }
}