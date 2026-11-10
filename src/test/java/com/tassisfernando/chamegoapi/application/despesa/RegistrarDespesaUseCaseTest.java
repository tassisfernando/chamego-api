package com.tassisfernando.chamegoapi.application.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.UnidadeMedida;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.despesa.TipoDespesa;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarDespesaUseCaseTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private RegistrarDespesaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarDespesaUseCase(despesaRepository, insumoRepository, movimentoEstoqueRepository);
        lenient().when(despesaRepository.salvar(any(Despesa.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void deveRegistrarCompraDeInsumoEAtualizarEstoque() {
        Insumo insumo = ceraComEstoque("0", "0.00");
        when(insumoRepository.buscarPorId(insumo.id())).thenReturn(Optional.of(insumo));

        Despesa despesa = useCase.executar(new RegistrarDespesaComando(
                "CERA DE COCO SOLVEN 1KG",
                LocalDate.of(2025, 12, 24),
                new BigDecimal("42.00"),
                new BigDecimal("1000"),
                UnidadeMedida.G,
                TipoDespesa.INSUMO,
                insumo.id(),
                null,
                null));

        assertEquals(quantidade("1000", UnidadeMedida.G), insumo.estoqueAtual());
        assertEquals(brl("0.04"), insumo.custoMedio());
        verify(insumoRepository).salvar(insumo);
        verify(movimentoEstoqueRepository).salvar(any());
        verify(despesaRepository).salvar(despesa);
    }

    @Test
    void naoDeveMovimentarEstoqueEmDespesaOperacional() {
        useCase.executar(new RegistrarDespesaComando(
                "HOT PRINT",
                LocalDate.of(2026, 3, 10),
                new BigDecimal("57.50"),
                null,
                null,
                TipoDespesa.OPERACIONAL,
                null,
                null,
                null));

        verify(insumoRepository, never()).buscarPorId(any());
        verify(movimentoEstoqueRepository, never()).salvar(any());
    }
}
