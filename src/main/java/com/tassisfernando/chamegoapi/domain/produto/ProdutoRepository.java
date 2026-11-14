package com.tassisfernando.chamegoapi.domain.produto;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository {

    Produto salvar(Produto produto);

    Optional<Produto> buscarPorId(UUID id);

    List<Produto> listarAtivos();

    Pagina<Produto> listarAtivos(int pagina, int tamanho);

    List<Produto> salvarTodos(List<Produto> produtos);
}
