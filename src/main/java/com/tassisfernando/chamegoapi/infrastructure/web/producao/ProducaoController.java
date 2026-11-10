package com.tassisfernando.chamegoapi.infrastructure.web.producao;

import com.tassisfernando.chamegoapi.application.producao.RegistrarProducaoComando;
import com.tassisfernando.chamegoapi.application.producao.RegistrarProducaoUseCase;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/producoes")
public class ProducaoController {

    private final RegistrarProducaoUseCase registrarProducaoUseCase;

    public ProducaoController(RegistrarProducaoUseCase registrarProducaoUseCase) {
        this.registrarProducaoUseCase = registrarProducaoUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> registrar(@Valid @RequestBody Requisicao requisicao) {
        LoteProducao lote = registrarProducaoUseCase.executar(new RegistrarProducaoComando(
                requisicao.produtoId(),
                requisicao.dataProducao(),
                requisicao.quantidadeProduzida(),
                requisicao.observacao()));
        return Map.of(
                "id", lote.id(),
                "custoUnitario", lote.custoUnitario().valor(),
                "custoTotal", lote.custoTotal().valor());
    }

    public record Requisicao(
            @NotNull UUID produtoId,
            LocalDate dataProducao,
            @Min(1) int quantidadeProduzida,
            String observacao) {
    }
}
