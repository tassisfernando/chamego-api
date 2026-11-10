package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.notificacao.NotificarAniversariantesPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WhatsAppAniversariantesAdapter implements NotificarAniversariantesPort {

    private static final Logger LOG = LoggerFactory.getLogger(WhatsAppAniversariantesAdapter.class);

    @Override
    public void notificar(List<Cliente> aniversariantes) {
        for (Cliente cliente : aniversariantes) {
            if (!cliente.temTelefone()) {
                continue;
            }
            LOG.info(
                    "Agendar cupom de aniversário via WhatsApp para {} ({})",
                    cliente.nome(),
                    cliente.telefone());
        }
    }
}
