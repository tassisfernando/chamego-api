package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastrarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CadastrarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente executar(CadastrarClienteComando comando) {
        Cliente cliente = Cliente.novo(
                comando.nome(),
                comando.telefone(),
                comando.cidade(),
                comando.estado(),
                comando.dataNascimento());
        return clienteRepository.salvar(cliente);
    }
}
