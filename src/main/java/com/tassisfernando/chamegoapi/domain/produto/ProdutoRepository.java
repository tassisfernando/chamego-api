package com.tassisfernando.chamegoapi.domain.produto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    Optional<Produto> buscarPorId(UUID id);

    List<Produto> listarAtivos();

    List<Produto> salvarTodos(List<Produto> produtos);
}
