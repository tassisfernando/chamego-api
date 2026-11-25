package com.tassisfernando.chamegoapi.application.notificacao;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MensagemResumoDiarioProprietariaTest {

    private final LocalDate hoje = LocalDate.of(2026, 11, 24);

    @Test
    void incluiAsDuasSecoesQuandoHaAniversarioEEntrega() {
        Optional<String> mensagem = MensagemResumoDiarioProprietaria.montar(
                "Maressa",
                hoje,
                List.of(new MensagemResumoDiarioProprietaria.AniversarianteLinha("Helena", "31988887777")),
                List.of(entrega()));

        assertTrue(mensagem.isPresent());
        String texto = mensagem.get();
        assertTrue(texto.startsWith("Bom dia, Maressa! 🕯️✨ Aqui está o resumo da Chamego para hoje (24/11):"));
        assertTrue(texto.contains("🎂 *Aniversariantes de Hoje:*"));
        assertTrue(texto.contains("• Helena - WhatsApp: https://wa.me/5531988887777"));
        assertTrue(texto.contains("📦 *Entregas Agendadas para Hoje:*"));
        assertTrue(texto.contains("2× Lavanda"));
        assertTrue(texto.contains("https://wa.me/5531999990000"));
    }

    @Test
    void omiteEntregasQuandoSoHaAniversario() {
        String texto = MensagemResumoDiarioProprietaria.montar(
                        "Maressa",
                        hoje,
                        List.of(new MensagemResumoDiarioProprietaria.AniversarianteLinha("Helena", null)),
                        List.of())
                .orElseThrow();

        assertTrue(texto.contains("*Aniversariantes de Hoje:*"));
        assertFalse(texto.contains("Entregas Agendadas"));
        assertFalse(texto.contains("WhatsApp:"));
    }

    @Test
    void omiteAniversariosQuandoSoHaEntrega() {
        String texto = MensagemResumoDiarioProprietaria.montar("Maressa", hoje, List.of(), List.of(entrega()))
                .orElseThrow();

        assertTrue(texto.contains("*Entregas Agendadas para Hoje:*"));
        assertFalse(texto.contains("Aniversariantes"));
    }

    @Test
    void naoMontaMensagemVazia() {
        assertTrue(MensagemResumoDiarioProprietaria.montar("Maressa", hoje, List.of(), List.of()).isEmpty());
        assertTrue(MensagemResumoDiarioProprietaria.montar("Maressa", hoje, null, null).isEmpty());
    }

    private static MensagemResumoDiarioProprietaria.EntregaLinha entrega() {
        return new MensagemResumoDiarioProprietaria.EntregaLinha(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "Lia",
                "31999990000",
                "2× Lavanda",
                new BigDecimal("65.80"));
    }
}
