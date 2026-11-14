package com.tassisfernando.chamegoapi.infrastructure.web.produto;

import com.tassisfernando.chamegoapi.application.produto.AtualizarProdutoComando;
import com.tassisfernando.chamegoapi.application.produto.AtualizarProdutoUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.produto.CategoriaProduto;
import com.tassisfernando.chamegoapi.domain.produto.ItemReceita;
import com.tassisfernando.chamegoapi.domain.produto.Produto;
import com.tassisfernando.chamegoapi.domain.produto.ProdutoRepository;
import com.tassisfernando.chamegoapi.infrastructure.web.compartilhado.PaginaResposta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;

    public ProdutoController(
            ProdutoRepository produtoRepository, AtualizarProdutoUseCase atualizarProdutoUseCase) {
        this.produtoRepository = produtoRepository;
        this.atualizarProdutoUseCase = atualizarProdutoUseCase;
    }

    @GetMapping
    public PaginaResposta<ProdutoResposta> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return PaginaResposta.mapear(
                produtoRepository.listarAtivos(pageable.getPageNumber(), pageable.getPageSize()),
                ProdutoResposta::de);
    }

    @GetMapping("/opcoes")
    public List<ProdutoResposta> opcoes() {
        return produtoRepository.listarAtivos().stream().map(ProdutoResposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProdutoResposta cadastrar(@Valid @RequestBody Requisicao requisicao) {
        Produto produto = Produto.novo(
                requisicao.nome(),
                requisicao.categoria() == null ? CategoriaProduto.VELA : requisicao.categoria(),
                requisicao.pesoGramas(),
                Dinheiro.de(requisicao.precoVenda() == null ? BigDecimal.ZERO : requisicao.precoVenda()),
                Dinheiro.de(requisicao.custoPadrao() == null ? BigDecimal.ZERO : requisicao.custoPadrao()));

        if (requisicao.receita() != null && !requisicao.receita().isEmpty()) {
            produto.definirReceita(requisicao.receita().stream()
                    .map(item -> new ItemReceita(
                            item.insumoId(),
                            Quantidade.de(
                                    item.quantidade(),
                                    item.unidadeMedida() == null ? UnidadeMedida.G : item.unidadeMedida())))
                    .toList());
        }

        return ProdutoResposta.de(produtoRepository.salvar(produto));
    }

    @PutMapping("/{id}")
    public ProdutoResposta atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarRequisicao requisicao) {
        Produto produto = atualizarProdutoUseCase.executar(new AtualizarProdutoComando(
                id,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.categoria(),
                requisicao.pesoGramas(),
                requisicao.precoVenda(),
                requisicao.custoPadrao(),
                requisicao.estoqueAtual()));
        return ProdutoResposta.de(produto);
    }

    public record Requisicao(
            @NotBlank String nome,
            CategoriaProduto categoria,
            Integer pesoGramas,
            BigDecimal precoVenda,
            BigDecimal custoPadrao,
            List<ItemReceitaRequisicao> receita) {
    }

    public record AtualizarRequisicao(
            @NotBlank String nome,
            String descricao,
            CategoriaProduto categoria,
            Integer pesoGramas,
            BigDecimal precoVenda,
            BigDecimal custoPadrao,
            Integer estoqueAtual) {
    }

    public record ItemReceitaRequisicao(
            @NotNull UUID insumoId,
            @NotNull BigDecimal quantidade,
            UnidadeMedida unidadeMedida) {
    }

    public record ProdutoResposta(
            UUID id,
            String nome,
            String descricao,
            CategoriaProduto categoria,
            Integer pesoGramas,
            BigDecimal precoVenda,
            BigDecimal custoPadrao,
            int estoqueAtual,
            boolean ativo,
            List<ItemReceitaResposta> receita) {

        static ProdutoResposta de(Produto produto) {
            return new ProdutoResposta(
                    produto.id(),
                    produto.nome(),
                    produto.descricao(),
                    produto.categoria(),
                    produto.pesoGramas(),
                    produto.precoVenda().valor(),
                    produto.custoPadrao().valor(),
                    produto.estoqueAtual(),
                    produto.ativo(),
                    produto.receita().stream()
                            .map(item -> new ItemReceitaResposta(
                                    item.insumoId(),
                                    item.quantidadePorUnidade().valor(),
                                    item.quantidadePorUnidade().unidade()))
                            .toList());
        }
    }

    public record ItemReceitaResposta(UUID insumoId, BigDecimal quantidade, UnidadeMedida unidadeMedida) {
    }
}
