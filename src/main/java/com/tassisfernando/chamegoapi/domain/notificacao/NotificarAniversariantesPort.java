package com.tassisfernando.chamegoapi.domain.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;

import java.util.List;

public interface NotificarAniversariantesPort {

    void notificar(List<Cliente> aniversariantes);
}
