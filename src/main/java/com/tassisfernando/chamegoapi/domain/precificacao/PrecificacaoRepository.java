package com.tassisfernando.chamegoapi.domain.precificacao;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.util.Optional;
import java.util.UUID;

public interface PrecificacaoRepository {

    Precificacao salvar(Precificacao precificacao);

    Optional<Precificacao> buscarPorId(UUID id);

    Pagina<Precificacao> listar(int pagina, int tamanho);

    void excluir(UUID id);
}