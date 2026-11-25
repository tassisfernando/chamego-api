package com.tassisfernando.chamegoapi.application.pedido;

import java.util.List;

public record PainelEntregas(List<EntregaAgendadaResumo> hoje, List<EntregaAgendadaResumo> proximas) {}
