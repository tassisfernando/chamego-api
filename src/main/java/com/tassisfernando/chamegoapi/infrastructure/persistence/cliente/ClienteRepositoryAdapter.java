package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.ClienteRepository;
import com.tassisfernando.chamegoapi.domain.compartilhado.Pagina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public List<Cliente> listarAtivos() {
        return jpaRepository.findByAtivoTrueOrderByNomeAsc().stream().map(mapper::paraDominio).toList();
    }

    @Override
    public Pagina<Cliente> listarAtivos(int pagina, int tamanho) {
        Page<ClienteJpaEntity> page =
                jpaRepository.findByAtivoTrue(PageRequest.of(pagina, tamanho, Sort.by("nome").ascending()));
        return Pagina.de(
                page.getContent().stream().map(mapper::paraDominio).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }

    @Override
    public Pagina<Cliente> buscarAtivos(String busca, int pagina, int tamanho) {
        String termo = busca == null ? "" : busca.trim();
        if (termo.isBlank()) {
            return listarAtivos(pagina, tamanho);
        }
        String digitos = termo.replaceAll("\\D+", "");
        Page<ClienteJpaEntity> page = jpaRepository.buscarAtivos(
                termo,
                digitos.isBlank() ? termo : digitos,
                PageRequest.of(pagina, tamanho, Sort.by("nome").ascending()));
        return Pagina.de(
                page.getContent().stream().map(mapper::paraDominio).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements());
    }

    @Override
    public List<Cliente> buscarAniversariantesDoDia(MonthDay dia) {
        return jpaRepository.buscarAniversariantes(dia.getMonthValue(), dia.getDayOfMonth()).stream()
                .map(mapper::paraDominio)
                .toList();
    }

    @Override
    public List<Cliente> listarAtivosComNascimento() {
        return jpaRepository.findByAtivoTrueAndDataNascimentoIsNotNull().stream()
                .map(mapper::paraDominio)
                .toList();
    }
}
