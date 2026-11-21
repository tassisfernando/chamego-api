package com.tassisfernando.chamegoapi.infrastructure.web.precificacao;

import com.tassisfernando.chamegoapi.application.precificacao.AplicarPrecoProdutoUseCase;
import com.tassisfernando.chamegoapi.application.precificacao.AtualizarPrecificacaoUseCase;
import com.tassisfernando.chamegoapi.application.precificacao.ExcluirPrecificacaoUseCase;
import com.tassisfernando.chamegoapi.application.precificacao.LinhaPrecificacaoComando;
import com.tassisfernando.chamegoapi.application.precificacao.PrecificacaoMontagemService;
import com.tassisfernando.chamegoapi.application.precificacao.PublicarKitComoProdutoUseCase;
import com.tassisfernando.chamegoapi.application.precificacao.SalvarPrecificacaoUseCase;
import com.tassisfernando.chamegoapi.application.precificacao.SimularPrecificacaoUseCase;
import com.tassisfernando.chamegoapi.domain.precificacao.Precificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.PrecificacaoRepository;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoPrecificacao;
import com.tassisfernando.chamegoapi.domain.precificacao.TipoItemPrecificacao;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.infrastructure.web.compartilhado.PaginaResposta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/precificacoes")
public class PrecificacaoController {

    private final PrecificacaoRepository precificacaoRepository;
    private final PrecificacaoMontagemService montagemService;
    private final SimularPrecificacaoUseCase simularPrecificacaoUseCase;
    private final SalvarPrecificacaoUseCase salvarPrecificacaoUseCase;
    private final AtualizarPrecificacaoUseCase atualizarPrecificacaoUseCase;
    private final ExcluirPrecificacaoUseCase excluirPrecificacaoUseCase;
    private final AplicarPrecoProdutoUseCase aplicarPrecoProdutoUseCase;
    private final PublicarKitComoProdutoUseCase publicarKitComoProdutoUseCase;

    public PrecificacaoController(
            PrecificacaoRepository precificacaoRepository,
            PrecificacaoMontagemService montagemService,
            SimularPrecificacaoUseCase simularPrecificacaoUseCase,
            SalvarPrecificacaoUseCase salvarPrecificacaoUseCase,
            AtualizarPrecificacaoUseCase atualizarPrecificacaoUseCase,
            ExcluirPrecificacaoUseCase excluirPrecificacaoUseCase,
            AplicarPrecoProdutoUseCase aplicarPrecoProdutoUseCase,
            PublicarKitComoProdutoUseCase publicarKitComoProdutoUseCase) {
        this.precificacaoRepository = precificacaoRepository;
        this.montagemService = montagemService;
        this.simularPrecificacaoUseCase = simularPrecificacaoUseCase;
        this.salvarPrecificacaoUseCase = salvarPrecificacaoUseCase;
        this.atualizarPrecificacaoUseCase = atualizarPrecificacaoUseCase;
        this.excluirPrecificacaoUseCase = excluirPrecificacaoUseCase;
        this.aplicarPrecoProdutoUseCase = aplicarPrecoProdutoUseCase;
        this.publicarKitComoProdutoUseCase = publicarKitComoProdutoUseCase;
    }

    @GetMapping
    public PaginaResposta<PrecificacaoResposta> listar(@PageableDefault(size = 20) Pageable pageable) {
        return PaginaResposta.mapear(
                precificacaoRepository.listar(pageable.getPageNumber(), pageable.getPageSize()),
                p -> PrecificacaoResposta.de(p, false));
    }

    @GetMapping("/{id}")
    public PrecificacaoResposta buscar(@PathVariable UUID id) {
        Precificacao p = precificacaoRepository
                .buscarPorId(id)
                .orElseThrow(() -> com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada.de(
                        "Precificação", id));
        return PrecificacaoResposta.de(p, true);
    }

    @GetMapping("/ficha-produto/{produtoId}")
    public List<ItemResposta> fichaProduto(@PathVariable UUID produtoId) {
        return montagemService.itensDaFichaTecnica(produtoId).stream()
                .map(ItemResposta::de)
                .toList();
    }

    @PostMapping("/simular")
    public SimulacaoResposta simular(@Valid @RequestBody RequisicaoSalvar requisicao) {
        var resultado = simularPrecificacaoUseCase.executar(
                new SimularPrecificacaoUseCase.SimularPrecificacaoComando(
                        mapearLinhas(requisicao.itens()),
                        requisicao.margemPercentual(),
                        requisicao.precoVenda()));
        return SimulacaoResposta.de(resultado);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrecificacaoResposta salvar(@Valid @RequestBody RequisicaoSalvar requisicao) {
        Precificacao p = salvarPrecificacaoUseCase.executar(new SalvarPrecificacaoUseCase.SalvarPrecificacaoComando(
                requisicao.titulo(),
                requisicao.tipo(),
                requisicao.produtoReferenciaId(),
                mapearLinhas(requisicao.itens()),
                requisicao.margemPercentual(),
                requisicao.precoVenda(),
                requisicao.observacao()));
        return PrecificacaoResposta.de(
                precificacaoRepository.buscarPorId(p.id()).orElse(p), true);
    }

    @PutMapping("/{id}")
    public PrecificacaoResposta atualizar(@PathVariable UUID id, @Valid @RequestBody RequisicaoAtualizar requisicao) {
        Precificacao p = atualizarPrecificacaoUseCase.executar(new AtualizarPrecificacaoUseCase.AtualizarPrecificacaoComando(
                id,
                requisicao.titulo(),
                requisicao.produtoReferenciaId(),
                mapearLinhas(requisicao.itens()),
                requisicao.margemPercentual(),
                requisicao.precoVenda(),
                requisicao.observacao()));
        return PrecificacaoResposta.de(
                precificacaoRepository.buscarPorId(p.id()).orElse(p), true);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        excluirPrecificacaoUseCase.executar(id);
    }

    @PostMapping("/{id}/aplicar-preco")
    public Map<String, Object> aplicarPreco(
            @PathVariable UUID id, @RequestParam(required = false) UUID produtoId) {
        var produto = aplicarPrecoProdutoUseCase.executar(id, produtoId);
        return Map.of("produtoId", produto.id(), "precoVenda", produto.precoVenda().valor());
    }

    @PostMapping("/{id}/publicar-kit")
    public Map<String, Object> publicarKit(
            @PathVariable UUID id, @RequestParam(required = false) String nomeProduto) {
        var produto = publicarKitComoProdutoUseCase.executar(id, nomeProduto);
        return Map.of("produtoId", produto.id(), "nome", produto.nome());
    }

    private List<LinhaPrecificacaoComando> mapearLinhas(List<LinhaRequisicao> itens) {
        return itens.stream()
                .map(linha -> new LinhaPrecificacaoComando(
                        linha.tipo(),
                        linha.produtoId(),
                        linha.insumoId(),
                        linha.descricao(),
                        linha.quantidade(),
                        linha.unidadeMedida(),
                        linha.custoUnitario()))
                .toList();
    }

    public record RequisicaoSalvar(
            @NotBlank String titulo,
            @NotNull TipoPrecificacao tipo,
            UUID produtoReferenciaId,
            @NotNull List<LinhaRequisicao> itens,
            BigDecimal margemPercentual,
            BigDecimal precoVenda,
            String observacao) {}

    public record RequisicaoAtualizar(
            @NotBlank String titulo,
            UUID produtoReferenciaId,
            @NotNull List<LinhaRequisicao> itens,
            BigDecimal margemPercentual,
            BigDecimal precoVenda,
            String observacao) {}

    public record LinhaRequisicao(
            @NotNull TipoItemPrecificacao tipo,
            UUID produtoId,
            UUID insumoId,
            String descricao,
            @NotNull BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            BigDecimal custoUnitario) {}
}