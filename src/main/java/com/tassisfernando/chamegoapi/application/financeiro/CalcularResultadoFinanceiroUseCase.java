package com.tassisfernando.chamegoapi.application.financeiro;

import com.tassisfernando.chamegoapi.domain.compartilhado.Dinheiro;
import com.tassisfernando.chamegoapi.domain.despesa.Despesa;
import com.tassisfernando.chamegoapi.domain.despesa.DespesaRepository;
import com.tassisfernando.chamegoapi.domain.pedido.Pedido;
import com.tassisfernando.chamegoapi.domain.pedido.PedidoRepository;
import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CalcularResultadoFinanceiroUseCase {

    private final PedidoRepository pedidoRepository;
    private final DespesaRepository despesaRepository;

    public CalcularResultadoFinanceiroUseCase(
            PedidoRepository pedidoRepository, DespesaRepository despesaRepository) {
        this.pedidoRepository = pedidoRepository;
        this.despesaRepository = despesaRepository;
    }

    @Transactional(readOnly = true)
    public ResultadoFinanceiro executar(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = pedidoRepository.listarPorPeriodo(inicio, fim).stream()
                .filter(Pedido::estaConfirmado)
                .filter(pedido -> pedido.status() != StatusPedido.CANCELADO)
                .toList();

        Dinheiro receita = pedidos.stream()
                .map(Pedido::receitaContabil)
                .reduce(Dinheiro.zero(), Dinheiro::somar);
        Dinheiro cmv = pedidos.stream().map(Pedido::custoTotal).reduce(Dinheiro.zero(), Dinheiro::somar);
        Dinheiro lucroBruto = receita.subtrair(cmv);

        Dinheiro despesas = despesaRepository.listarPorPeriodo(inicio, fim).stream()
                .map(Despesa::valor)
                .reduce(Dinheiro.zero(), Dinheiro::somar);

        return new ResultadoFinanceiro(
                inicio, fim, receita, cmv, lucroBruto, despesas, lucroBruto.subtrair(despesas));
    }
}
