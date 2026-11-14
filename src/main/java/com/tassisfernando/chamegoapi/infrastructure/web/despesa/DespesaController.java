package com.tassisfernando.chamegoapi.infrastructure.web.despesa;

import com.tassisfernando.chamegoapi.application.despesa.AtualizarDespesaComando;
import com.tassisfernando.chamegoapi.application.despesa.AtualizarDespesaUseCase;
import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaComando;
import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.PeriodoOperacao;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import com.tassisfernando.chamegoapi.infrastructure.web.compartilhado.PaginaResposta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final RegistrarDespesaUseCase registrarDespesaUseCase;
    private final AtualizarDespesaUseCase atualizarDespesaUseCase;
    private final DespesaRepository despesaRepository;

    public DespesaController(
            RegistrarDespesaUseCase registrarDespesaUseCase,
            AtualizarDespesaUseCase atualizarDespesaUseCase,
            DespesaRepository despesaRepository) {
        this.registrarDespesaUseCase = registrarDespesaUseCase;
        this.atualizarDespesaUseCase = atualizarDespesaUseCase;
        this.despesaRepository = despesaRepository;
    }

    @GetMapping
    public PaginaResposta<DespesaResposta> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @PageableDefault(size = 20, sort = "dataDespesa") Pageable pageable) {
        LocalDate inicioResolvido = PeriodoOperacao.inicioEfetivo(inicio);
        LocalDate fimResolvido = PeriodoOperacao.fimEfetivo(fim);
        return PaginaResposta.mapear(
                despesaRepository.listarPorPeriodo(
                        inicioResolvido, fimResolvido, pageable.getPageNumber(), pageable.getPageSize()),
                DespesaResposta::de);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registrar(@Valid @RequestBody Requisicao requisicao) {
        Despesa despesa = registrarDespesaUseCase.executar(new RegistrarDespesaComando(
                requisicao.descricao(),
                requisicao.dataDespesa(),
                requisicao.valor(),
                requisicao.quantidade(),
                requisicao.unidadeMedida(),
                requisicao.tipo(),
                requisicao.insumoId(),
                requisicao.ferramentaId(),
                requisicao.observacao()));
        return Map.of("id", despesa.id(), "valor", despesa.valor().valor(), "tipo", despesa.tipo());
    }

    @PutMapping("/{id}")
    public DespesaResposta atualizar(@PathVariable UUID id, @Valid @RequestBody Requisicao requisicao) {
        Despesa despesa = atualizarDespesaUseCase.executar(new AtualizarDespesaComando(
                id,
                requisicao.descricao(),
                requisicao.dataDespesa(),
                requisicao.valor(),
                requisicao.quantidade(),
                requisicao.unidadeMedida(),
                requisicao.tipo(),
                requisicao.insumoId(),
                requisicao.ferramentaId(),
                requisicao.observacao()));
        return DespesaResposta.de(despesa);
    }

    public record Requisicao(
            @NotBlank String descricao,
            LocalDate dataDespesa,
            @NotNull BigDecimal valor,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            @NotNull TipoDespesa tipo,
            UUID insumoId,
            UUID ferramentaId,
            String observacao) {
    }

    public record DespesaResposta(
            UUID id,
            String descricao,
            LocalDate dataDespesa,
            BigDecimal valor,
            BigDecimal quantidade,
            UnidadeMedida unidadeMedida,
            TipoDespesa tipo,
            UUID insumoId,
            UUID ferramentaId,
            String observacao) {

        static DespesaResposta de(Despesa despesa) {
            return new DespesaResposta(
                    despesa.id(),
                    despesa.descricao(),
                    despesa.dataDespesa(),
                    despesa.valor().valor(),
                    despesa.quantidade() == null ? null : despesa.quantidade().valor(),
                    despesa.quantidade() == null ? null : despesa.quantidade().unidade(),
                    despesa.tipo(),
                    despesa.insumoId(),
                    despesa.ferramentaId(),
                    despesa.observacao());
        }
    }
}
