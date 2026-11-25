package com.tassisfernando.chamegoapi.application.pedido;

import com.tassisfernando.chamegoapi.domain.pedido.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EntregaAgendadaResumo(
        UUID id,
        UUID clienteId,
        String clienteNome,
        String clienteTelefone,
        LocalDate dataEntrega,
        StatusPedido status,
        String resumoItens,
        BigDecimal valorTotal,
        boolean hoje,
        int diasAteEntrega) {}
