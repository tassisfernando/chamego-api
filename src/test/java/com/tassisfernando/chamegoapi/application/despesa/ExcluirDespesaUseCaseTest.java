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

import java.time.LocalDate;
import java.util.Optional;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.brl;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.ceraComEstoque;
import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.quantidade;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExcluirDespesaUseCaseTest {

    @Mock
    private DespesaRepository despesaRepository;

    @Mock
    private InsumoRepository insumoRepository;

    @Mock
    private MovimentoEstoqueRepository movimentoEstoqueRepository;

    private ExcluirDespesaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ExcluirDespesaUseCase(despesaRepository, insumoRepository, movimentoEstoqueRepository);
    }

    @Test
    void excluiDespesaOperacionalSemMovimentarEstoque() {
        Despesa despesa = Despesa.nova(
                "Frete",
                LocalDate.now(),
                brl("50"),
                null,
                TipoDespesa.OPERACIONAL,
                null,
                null,
                null);
        when(despesaRepository.buscarPorId(despesa.id())).thenReturn(Optional.of(despesa));

        useCase.executar(despesa.id());

        verify(despesaRepository).excluir(despesa.id());
        verify(insumoRepository, never()).salvar(any());
    }

    @Test
    void reverteEntradaDeInsumoAoExcluirCompra() {
        Insumo insumo = ceraComEstoque("500", "0.05");
        Despesa despesa = Despesa.nova(
                "Compra cera",
                LocalDate.now(),
                brl("25"),
                quantidade("100", UnidadeMedida.G),
                TipoDespesa.INSUMO,
                insumo.id(),
                null,
                null);
        when(despesaRepository.buscarPorId(despesa.id())).thenReturn(Optional.of(despesa));
        when(insumoRepository.buscarPorId(insumo.id())).thenReturn(Optional.of(insumo));

        useCase.executar(despesa.id());

        verify(movimentoEstoqueRepository).salvar(any());
        verify(insumoRepository).salvar(insumo);
        verify(despesaRepository).excluir(despesa.id());
    }
}