package com.tassisfernando.chamegoapi.domain.despesa;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DespesaRepository {

    Despesa salvar(Despesa despesa);

    Optional<Despesa> buscarPorId(UUID id);

    List<Despesa> listarPorPeriodo(LocalDate inicio, LocalDate fim);
}
