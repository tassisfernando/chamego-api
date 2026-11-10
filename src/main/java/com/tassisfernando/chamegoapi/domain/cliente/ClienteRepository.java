package com.tassisfernando.chamegoapi.domain.cliente;

import java.time.MonthDay;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    List<Cliente> listarTodos();

    List<Cliente> buscarAniversariantesDoDia(MonthDay dia);
}
