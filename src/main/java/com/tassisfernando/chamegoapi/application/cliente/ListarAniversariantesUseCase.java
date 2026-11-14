package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.MonthDay;
import java.util.List;

@Service
public class ListarAniversariantesUseCase {

    private final ClienteRepository clienteRepository;

    public ListarAniversariantesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> executar(MonthDay dia) {
        return clienteRepository.buscarAniversariantesDoDia(dia);
    }
}
