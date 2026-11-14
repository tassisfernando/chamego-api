package com.tassisfernando.chamegoapi.application.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.Quantidade;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoque;
import com.tassisfernando.chamegoapi.domain.estoque.MovimentoEstoqueRepository;
import com.tassisfernando.chamegoapi.domain.estoque.OrigemMovimento;
import com.tassisfernando.chamegoapi.domain.insumo.Insumo;
import com.tassisfernando.chamegoapi.domain.insumo.InsumoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AtualizarInsumoUseCase {

    private final InsumoRepository insumoRepository;
    private final MovimentoEstoqueRepository movimentoEstoqueRepository;

    public AtualizarInsumoUseCase(
            InsumoRepository insumoRepository, MovimentoEstoqueRepository movimentoEstoqueRepository) {
        this.insumoRepository = insumoRepository;
        this.movimentoEstoqueRepository = movimentoEstoqueRepository;
    }

    @Transactional
    public Insumo executar(AtualizarInsumoComando comando) {
        Insumo insumo = insumoRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Insumo", comando.id()));

        insumo.atualizarDados(
                comando.nome(),
                comando.categoria(),
                comando.marca(),
                comando.unidadeMedida() == null ? insumo.unidadeMedida() : comando.unidadeMedida());

        if (comando.custoMedio() != null) {
            insumo.definirCustoMedio(Dinheiro.de(comando.custoMedio()));
        }

        if (comando.estoqueAtual() != null) {
            BigDecimal anterior = insumo.estoqueAtual().valor();
            BigDecimal novo = comando.estoqueAtual();
            BigDecimal delta = novo.subtract(anterior);
            Quantidade novoEstoque = Quantidade.de(novo, insumo.unidadeMedida());
            insumo.definirEstoque(novoEstoque);
            if (delta.compareTo(BigDecimal.ZERO) > 0) {
                movimentoEstoqueRepository.salvar(MovimentoEstoque.entradaInsumo(
                        insumo.id(),
                        Quantidade.de(delta, insumo.unidadeMedida()),
                        OrigemMovimento.AJUSTE,
                        insumo.id(),
                        LocalDate.now()));
            } else if (delta.compareTo(BigDecimal.ZERO) < 0) {
                movimentoEstoqueRepository.salvar(MovimentoEstoque.saidaInsumo(
                        insumo.id(),
                        Quantidade.de(delta.abs(), insumo.unidadeMedida()),
                        OrigemMovimento.AJUSTE,
                        insumo.id(),
                        LocalDate.now()));
            }
        }

        return insumoRepository.salvar(insumo);
    }
}
