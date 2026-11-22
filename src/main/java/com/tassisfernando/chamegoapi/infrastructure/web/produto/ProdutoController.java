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
import com.tassisfernando.chamegoapi.application.produto.ExcluirProdutoUseCase;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;
    private final AtualizarProdutoUseCase atualizarProdutoUseCase;
    private final ExcluirProdutoUseCase excluirProdutoUseCase;

    public ProdutoController(
            ProdutoRepository produtoRepository,
            AtualizarProdutoUseCase atualizarProdutoUseCase,
            ExcluirProdutoUseCase excluirProdutoUseCase) {
        this.produtoRepository = produtoRepository;
        this.atualizarProdutoUseCase = atualizarProdutoUseCase;
        this.excluirProdutoUseCase = excluirProdutoUseCase;
    }

    @GetMapping
    public PaginaResposta<ProdutoResposta> listar(
            @RequestParam(required = false) String busca,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return PaginaResposta.mapear(
                produtoRepository.buscarAtivos(busca, pageable.getPageNumber(), pageable.getPageSize()),
                ProdutoResposta::de);
    }

    @GetMapping("/opcoes")
    public List<ProdutoResposta> opcoes(@RequestParam(required = false, name = "search") String search) {
        String termo = search == null ? "" : search.trim();
        if (!termo.isBlank()) {
            return produtoRepository.buscarAtivos(termo, 0, 50).conteudo().stream()
                    .map(ProdutoResposta::de)
                    .toList();
        }
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
            produto.definirReceita(consolidarReceita(requisicao.receita()));
        }

        return ProdutoResposta.de(produtoRepository.salvar(produto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirProdutoUseCase.executar(id);
    }

    @PutMapping("/{id}")
    public ProdutoResposta atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarRequisicao requisicao) {
        var receita = requisicao.receita() == null
                ? null
                : (requisicao.receita().isEmpty() ? List.<ItemReceita>of() : consolidarReceita(requisicao.receita()));
        Produto produto = atualizarProdutoUseCase.executar(new AtualizarProdutoComando(
                id,
                requisicao.nome(),
                requisicao.descricao(),
                requisicao.categoria(),
                requisicao.pesoGramas(),
                requisicao.precoVenda(),
                requisicao.custoPadrao(),
                requisicao.estoqueAtual(),
                receita));
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
            Integer estoqueAtual,
            List<ItemReceitaRequisicao> receita) {
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

    /** Agrupa por insumoId e soma quantidades (evita violar uq_produto_insumo). */
    private static List<ItemReceita> consolidarReceita(List<ItemReceitaRequisicao> receita) {
        Map<UUID, BigDecimal> quantidades = new LinkedHashMap<>();
        Map<UUID, UnidadeMedida> unidades = new LinkedHashMap<>();
        for (ItemReceitaRequisicao item : receita) {
            quantidades.merge(item.insumoId(), item.quantidade(), BigDecimal::add);
            unidades.putIfAbsent(
                    item.insumoId(),
                    item.unidadeMedida() == null ? UnidadeMedida.G : item.unidadeMedida());
        }
        List<ItemReceita> itens = new ArrayList<>();
        for (Map.Entry<UUID, BigDecimal> entry : quantidades.entrySet()) {
            UUID insumoId = entry.getKey();
            itens.add(new ItemReceita(
                    insumoId,
                    Quantidade.de(entry.getValue(), unidades.get(insumoId))));
        }
        return itens;
    }
}
