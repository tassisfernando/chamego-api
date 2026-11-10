package com.tassisfernando.chamegoapi.infrastructure.web.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteWebMapper {

    @Mapping(target = "id", expression = "java(cliente.id())")
    @Mapping(target = "nome", expression = "java(cliente.nome())")
    @Mapping(target = "telefone", expression = "java(cliente.telefone())")
    @Mapping(target = "cidade", expression = "java(cliente.cidade())")
    @Mapping(target = "estado", expression = "java(cliente.estado())")
    @Mapping(target = "dataCadastro", expression = "java(cliente.dataCadastro())")
    @Mapping(target = "dataNascimento", expression = "java(cliente.dataNascimento())")
    @Mapping(target = "statusCupom", expression = "java(cliente.statusCupom())")
    ClienteResposta paraResposta(Cliente cliente);
}
