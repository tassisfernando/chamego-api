package com.tassisfernando.chamegoapi.infrastructure.web.insumo;

import com.tassisfernando.chamegoapi.application.insumo.AtualizarInsumoComando;
import com.tassisfernando.chamegoapi.application.insumo.AtualizarInsumoUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.insumo.CategoriaInsumo;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
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
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoRepository insumoRepository;
    private final AtualizarInsumoUseCase atualizarInsumoUseCase;

    public InsumoController(InsumoRepository insumoRepository, AtualizarInsumoUseCase atualizarInsumoUseCase) {
        this.insumoRepository = insumoRepository;
        this.atualizarInsumoUseCase = atualizarInsumoUseCase;
    }

    @GetMapping
    public PaginaResposta<InsumoResposta> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return PaginaResposta.mapear(
                insumoRepository.listarAtivos(pageable.getPageNumber(), pageable.getPageSize()),
                InsumoResposta::de);
    }

    @GetMapping("/opcoes")
    public List<InsumoResposta> opcoes() {
        return insumoRepository.listarAtivos().stream().map(InsumoResposta::de).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InsumoResposta cadastrar(@Valid @RequestBody Requisicao requisicao) {
        Insumo insumo = Insumo.novo(
                requisicao.nome(),
                requisicao.categoria(),
                requisicao.marca(),
                requisicao.unidadeMedida());
        return InsumoResposta.de(insumoRepository.salvar(insumo));
    }

    @PutMapping("/{id}")
    public InsumoResposta atualizar(@PathVariable UUID id, @Valid @RequestBody AtualizarRequisicao requisicao) {
        Insumo insumo = atualizarInsumoUseCase.executar(new AtualizarInsumoComando(
                id,
                requisicao.nome(),
                requisicao.categoria(),
                requisicao.marca(),
                requisicao.unidadeMedida(),
                requisicao.estoqueAtual(),
                requisicao.custoMedio()));
        return InsumoResposta.de(insumo);
    }

    public record Requisicao(
            @NotBlank String nome,
            @NotNull CategoriaInsumo categoria,
            String marca,
            @NotNull UnidadeMedida unidadeMedida) {
    }

    public record AtualizarRequisicao(
            @NotBlank String nome,
            @NotNull CategoriaInsumo categoria,
            String marca,
            UnidadeMedida unidadeMedida,
            BigDecimal estoqueAtual,
            BigDecimal custoMedio) {
    }

    public record InsumoResposta(
            UUID id,
            String nome,
            CategoriaInsumo categoria,
            String marca,
            UnidadeMedida unidadeMedida,
            BigDecimal estoqueAtual,
            BigDecimal custoMedio,
            boolean ativo) {

        static InsumoResposta de(Insumo insumo) {
            return new InsumoResposta(
                    insumo.id(),
                    insumo.nome(),
                    insumo.categoria(),
                    insumo.marca(),
                    insumo.unidadeMedida(),
                    insumo.estoqueAtual().valor(),
                    insumo.custoMedio().valor(),
                    insumo.ativo());
        }
    }
}
