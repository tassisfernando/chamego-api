package com.tassisfernando.chamegoapi.domain.ferramenta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FerramentaRepository {

    Ferramenta salvar(Ferramenta ferramenta);

    Optional<Ferramenta> buscarPorId(UUID id);

    List<Ferramenta> listarAtivas();
}
