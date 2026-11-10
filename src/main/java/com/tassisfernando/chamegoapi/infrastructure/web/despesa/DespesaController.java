package com.tassisfernando.chamegoapi.infrastructure.web.despesa;

import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaComando;
import com.tassisfernando.chamegoapi.application.despesa.RegistrarDespesaUseCase;
import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    public DespesaController(RegistrarDespesaUseCase registrarDespesaUseCase) {
        this.registrarDespesaUseCase = registrarDespesaUseCase;
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
}
