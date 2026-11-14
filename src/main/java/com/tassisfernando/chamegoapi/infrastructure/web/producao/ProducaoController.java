package com.tassisfernando.chamegoapi.infrastructure.web.producao;

import com.tassisfernando.chamegoapi.application.producao.RegistrarProducaoComando;
import com.tassisfernando.chamegoapi.application.producao.RegistrarProducaoUseCase;
import com.tassisfernando.chamegoapi.domain.producao.LoteProducao;
import com.tassisfernando.chamegoapi.domain.producao.ProducaoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/producoes")
public class ProducaoController {

    private final RegistrarProducaoUseCase registrarProducaoUseCase;
    private final ProducaoRepository producaoRepository;

    public ProducaoController(
            RegistrarProducaoUseCase registrarProducaoUseCase, ProducaoRepository producaoRepository) {
        this.registrarProducaoUseCase = registrarProducaoUseCase;
        this.producaoRepository = producaoRepository;
    }

    @GetMapping
    public List<ProducaoResposta> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return producaoRepository.listarPorPeriodo(inicio, fim).stream().map(ProducaoResposta::de).toList();
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

    public record ProducaoResposta(
            UUID id,
            UUID produtoId,
            LocalDate dataProducao,
            int quantidadeProduzida,
            BigDecimal custoUnitario,
            BigDecimal custoTotal,
            String observacao) {

        static ProducaoResposta de(LoteProducao lote) {
            return new ProducaoResposta(
                    lote.id(),
                    lote.produtoId(),
                    lote.dataProducao(),
                    lote.quantidadeProduzida(),
                    lote.custoUnitario().valor(),
                    lote.custoTotal().valor(),
                    lote.observacao());
        }
    }
}
