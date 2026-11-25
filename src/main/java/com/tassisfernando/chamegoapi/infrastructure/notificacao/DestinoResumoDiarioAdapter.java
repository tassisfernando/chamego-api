package com.tassisfernando.chamegoapi.infrastructure.notificacao;

import com.tassisfernando.chamegoapi.application.notificacao.DestinoResumoDiarioPort;
import com.tassisfernando.chamegoapi.infrastructure.config.ChamegoNotificacaoProperties;
import org.springframework.stereotype.Component;

@Component
public class DestinoResumoDiarioAdapter implements DestinoResumoDiarioPort {

    private final ChamegoNotificacaoProperties properties;

    public DestinoResumoDiarioAdapter(ChamegoNotificacaoProperties properties) {
        this.properties = properties;
    }

    @Override
    public String nomeProprietaria() {
        return properties.ownerName();
    }

    @Override
    public String whatsappProprietaria() {
        return properties.ownerWhatsapp();
    }
}
