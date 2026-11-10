package com.tassisfernando.chamegoapi.infrastructure.web.financeiro;

import com.tassisfernando.chamegoapi.application.financeiro.CalcularResultadoFinanceiroUseCase;
import com.tassisfernando.chamegoapi.application.financeiro.ResultadoFinanceiro;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    private final CalcularResultadoFinanceiroUseCase calcularResultadoFinanceiroUseCase;

    public FinanceiroController(CalcularResultadoFinanceiroUseCase calcularResultadoFinanceiroUseCase) {
        this.calcularResultadoFinanceiroUseCase = calcularResultadoFinanceiroUseCase;
    }

    @GetMapping("/resultado")
    public Map<String, Object> resultado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        ResultadoFinanceiro resultado = calcularResultadoFinanceiroUseCase.executar(inicio, fim);
        return Map.of(
                "inicio", resultado.inicio(),
                "fim", resultado.fim(),
                "receitaVendas", resultado.receitaVendas().valor(),
                "custoMercadoriaVendida", resultado.custoMercadoriaVendida().valor(),
                "lucroBruto", resultado.lucroBruto().valor(),
                "totalDespesas", resultado.totalDespesas().valor(),
                "resultadoLiquido", resultado.resultadoLiquido().valor());
    }
}
