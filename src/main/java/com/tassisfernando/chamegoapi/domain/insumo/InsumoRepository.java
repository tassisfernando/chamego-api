package com.tassisfernando.chamegoapi.domain.insumo;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InsumoRepository {

    Insumo salvar(Insumo insumo);

    Optional<Insumo> buscarPorId(UUID id);

    List<Insumo> listarAtivos();

    Pagina<Insumo> listarAtivos(int pagina, int tamanho);

    List<Insumo> salvarTodos(List<Insumo> insumos);
}
