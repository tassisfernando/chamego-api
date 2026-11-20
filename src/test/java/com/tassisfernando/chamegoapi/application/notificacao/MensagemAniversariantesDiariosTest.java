package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.tassisfernando.chamegoapi.suporte.FixturesDominio.cliente;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensagemAniversariantesDiariosTest {

    @Test
    void retornaVazioQuandoNaoHaAniversariantes() {
        assertTrue(MensagemAniversariantesDiarios.montar(List.of(), LocalDate.of(2026, 3, 5)).isEmpty());
    }

    @Test
    void incluiNomeWhatsAppENota() {
        Cliente c = cliente("Maria");
        String texto = MensagemAniversariantesDiarios.montar(List.of(c), LocalDate.of(2026, 3, 5))
                .orElseThrow();
        assertTrue(texto.contains("Maria"));
        assertTrue(texto.contains("wa.me"));
        assertFalse(texto.isBlank());
    }
}