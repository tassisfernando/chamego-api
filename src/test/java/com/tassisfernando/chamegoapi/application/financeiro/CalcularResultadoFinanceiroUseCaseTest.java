package com.tassisfernando.chamegoapi.application.financeiro;

import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.pedido.ItemPedido;
import com.tassisfernando.chamegoapi.domain.pedido.NaturezaPedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.despesaOperacional;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.pedidoConfirmado;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularResultadoFinanceiroUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DespesaRepository despesaRepository;

    private CalcularResultadoFinanceiroUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalcularResultadoFinanceiroUseCase(pedidoRepository, despesaRepository);
    }

    @Test
    void deveCalcularLucroLiquidoComSucesso() {
        LocalDate inicio = LocalDate.of(2026, 3, 1);
        LocalDate fim = LocalDate.of(2026, 3, 31);
        var item = new ItemPedido(UUID.randomUUID(), 1, brl("32.90"), brl("16.45"));

        when(pedidoRepository.listarPorPeriodo(inicio, fim))
                .thenReturn(List.of(
                        pedidoConfirmado(NaturezaPedido.VENDA, item),
                        pedidoConfirmado(NaturezaPedido.DOACAO, item)));
        when(despesaRepository.listarPorPeriodo(inicio, fim)).thenReturn(List.of(despesaOperacional("10.00")));

        ResultadoFinanceiro resultado = useCase.executar(inicio, fim);

        assertEquals(brl("32.90"), resultado.receitaVendas());
        assertEquals(brl("32.90"), resultado.custoMercadoriaVendida());
        assertEquals(brl("0.00"), resultado.lucroBruto());
        assertEquals(brl("10.00"), resultado.totalDespesas());
        assertEquals(brl("-10.00"), resultado.resultadoLiquido());
    }
}
