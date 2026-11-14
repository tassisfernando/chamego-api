package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.StatusCupom;
import org.springframework.stereotype.Component;

@Component
public class ClientePersistenceMapper {

    public ClienteJpaEntity paraJpa(Cliente cliente) {
        ClienteJpaEntity entidade = new ClienteJpaEntity();
        entidade.setId(cliente.id());
        entidade.setNome(cliente.nome());
        entidade.setTelefone(cliente.telefone());
        entidade.setCidade(cliente.cidade());
        entidade.setEstado(cliente.estado());
        entidade.setDataCadastro(cliente.dataCadastro());
        entidade.setDataNascimento(cliente.dataNascimento());
        entidade.setStatusCupom(cliente.statusCupom().name());
        entidade.setObservacao(cliente.observacao());
        entidade.setAtivo(cliente.ativo());
        return entidade;
    }

    public Cliente paraDominio(ClienteJpaEntity entidade) {
        if (entidade == null) {
            return null;
        }
        return Cliente.reconstituir(
                entidade.getId(),
                entidade.getNome(),
                entidade.getTelefone(),
                entidade.getCidade(),
                entidade.getEstado(),
                entidade.getDataCadastro(),
                entidade.getDataNascimento(),
                StatusCupom.valueOf(entidade.getStatusCupom()),
                entidade.getObservacao(),
                entidade.isAtivo());
    }
}
