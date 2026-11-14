package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.EntidadeNaoEncontrada;
import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtualizarClienteUseCase {

    private final ClienteRepository clienteRepository;

    public AtualizarClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Cliente executar(AtualizarClienteComando comando) {
        Cliente cliente = clienteRepository
                .buscarPorId(comando.id())
                .orElseThrow(() -> EntidadeNaoEncontrada.de("Cliente", comando.id()));
        if (!cliente.ativo()) {
            throw new RegraDeNegocioViolada("não é possível editar cliente desativado");
        }
        cliente.atualizarDados(
                comando.nome(),
                comando.telefone(),
                comando.cidade(),
                comando.estado(),
                comando.dataNascimento());
        return clienteRepository.salvar(cliente);
    }
}
