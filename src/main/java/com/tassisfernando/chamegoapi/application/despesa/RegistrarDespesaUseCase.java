package com.tassisfernando.chamegoapi.application.despesa;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrarDespesaUseCase {

    private final DespesaRepository despesaRepository;
    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public RegistrarDespesaUseCase(
            DespesaRepository despesaRepository,
            InsumoRepository insumoRepository,
            MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.despesaRepository = despesaRepository;
        this.insumoRepository = insumoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public Despesa executar(RegistrarDespesaComando comando) {
        Quantidade quantidade = comando.quantidade() == null || comando.unidadeMedida() == null
                ? null
                : Quantidade.de(comando.quantidade(), comando.unidadeMedida());

        Despesa despesa = Despesa.nova(
                comando.descricao(),
                comando.dataDespesa(),
                Dinheiro.de(comando.valor()),
                quantidade,
                comando.tipo(),
                comando.insumoId(),
                comando.ferramentaId(),
                comando.observacao());

        if (despesa.atualizaEstoqueInsumo()) {
            Insumo insumo = insumoRepository
                    .buscarPorId(despesa.insumoId())
                    .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", despesa.insumoId()));
            insumo.registrarEntrada(despesa.quantidade(), despesa.custoUnitario());
            insumoRepository.salvar(insumo);
            movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaInsumo(
                    insumo.id(),
                    despesa.quantidade(),
                    OrigemMovimento.COMPRA,
                    despesa.id(),
                    despesa.dataDespesa()));
        }

        return despesaRepository.salvar(despesa);
    }
}
