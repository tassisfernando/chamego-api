package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DesativarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public DesativarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente executar(UUID id) {
        Cliente cliente = clienteRepository
                .buscarPorId(id)
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Cliente", id));
        cliente.desativar();
        return clienteRepository.salvar(cliente);
    }
}
