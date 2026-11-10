package com.tassisfernando.chamegoapi.domain.pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository {

    Pedido salvar(Pedido pedido);

    Optional<Pedido> buscarPorId(UUID id);

    List<Pedido> listarPorPeriodo(LocalDate inicio, LocalDate fim);

    List<Pedido> listarPorCliente(UUID clienteId);
}
