package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.time.MonthDay;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final ClienteJpaRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    public ClienteRepositoryAdapter(ClienteJpaRepository jpaRepository, ClientePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        return mapper.paraDominio(jpaRepository.save(mapper.paraJpa(cliente)));
    }

    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return jpaRepository.findById(id).map(mapper::paraDominio);
    }

    @Override
    public List<Cliente> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::paraDominio).toList();
    }

    @Override
    public List<Cliente> buscarAniversariantesDoDia(MonthDay dia) {
        return jpaRepository.buscarAniversariantes(dia.getMonthValue(), dia.getDayOfMonth()).stream()
                .map(mapper::paraDominio)
                .toList();
    }
}
