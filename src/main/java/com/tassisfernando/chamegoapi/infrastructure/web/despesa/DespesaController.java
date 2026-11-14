package com.tassisfernando.chamegoapi.infrastructure.web.despesa;

import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaComando;
import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {

    private final RegistrarDespesaUseCase registrarDespesaUseCase;
    private final DespesaRepository despesaRepository;

    public DespesaController(
            RegistrarDespesaUseCase registrarDespesaUseCase, DespesaRepository despesaRepository) {
        this.registrarDespesaUseCase = registrarDespesaUseCase;
        this.despesaRepository = despesaRepository;
    }

    @GetMapping
    public List<DespesaResposta> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return despesaRepository.listarPorPeriodo(inicio, fim).stream().map(DespesaResposta::de).toList();
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
