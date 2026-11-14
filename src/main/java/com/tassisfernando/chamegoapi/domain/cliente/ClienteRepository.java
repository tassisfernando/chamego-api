package com.tassisfernando.chamegoapi.domain.cliente;

import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;

import java.time.MonthDay;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepository {

    Cliente salvar(Cliente cliente);

    Optional<Cliente> buscarPorId(UUID id);

    List<Cliente> listarAtivos();

    Pagina<Cliente> listarAtivos(int pagina, int tamanho);

    Pagina<Cliente> buscarAtivos(String busca, int pagina, int tamanho);

    List<Cliente> buscarAniversariantesDoDia(MonthDay dia);

    List<Cliente> listarAtivosComNascimento();
}
