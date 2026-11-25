package com.tassisfernando.chamegoapi.application.notificacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

/** Texto do resumo diário enviado à proprietária da Chamego. */
public final class MensagemResumoDiarioProprietaria {

    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("dd/MM");

    private MensagemResumoDiarioProprietaria() {}

    public record AniversarianteLinha(String nome, String telefone) {}

    public record EntregaLinha(UUID pedidoId, String clienteNome, String telefone, String resumo, BigDecimal valor) {}

    public static Optional<String> montar(
            String nomeProprietaria,
            LocalDate referencia,
            List<AniversarianteLinha> aniversariantes,
            List<EntregaLinha> entregas) {
        boolean temAniversario = aniversariantes != null && !aniversariantes.isEmpty();
        boolean temEntrega = entregas != null && !entregas.isEmpty();
        if (!temAniversario && !temEntrega) {
            return Optional.empty();
        }
        String nome = nomeProprietaria == null || nomeProprietaria.isBlank() ? "Maressa" : nomeProprietaria.trim();
        var moeda = java.text.NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        var sb = new StringBuilder();
        sb.append("Bom dia, ")
                .append(nome)
                .append("! 🕯️✨ Aqui está o resumo da Chamego para hoje (")
                .append(DIA.format(referencia))
                .append("):\n");
        if (temAniversario) {
            sb.append("\n🎂 *Aniversariantes de Hoje:*\n");
            for (AniversarianteLinha linha : aniversariantes) {
                sb.append("• ").append(texto(linha.nome(), "Cliente"));
                anexarWhatsApp(sb, linha.telefone());
                sb.append('\n');
            }
        }
        if (temEntrega) {
            sb.append("\n📦 *Entregas Agendadas para Hoje:*\n");
            for (EntregaLinha linha : entregas) {
                sb.append("• Pedido #").append(linha.pedidoId());
                sb.append(" - ").append(texto(linha.clienteNome(), "Cliente"));
                sb.append(" (").append(texto(linha.resumo(), "itens"));
                sb.append(" / ").append(moeda.format(linha.valor() == null ? BigDecimal.ZERO : linha.valor()));
                sb.append(')');
                anexarWhatsApp(sb, linha.telefone());
                sb.append('\n');
            }
        }
        return Optional.of(sb.toString().trim());
    }

    static String linkWhatsApp(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return null;
        }
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("0")) {
            digitos = digitos.replaceFirst("^0+", "");
        }
        if (digitos.startsWith("55") && digitos.length() >= 12) {
            return "https://wa.me/" + digitos;
        }
        if (digitos.length() >= 10) {
            return "https://wa.me/55" + digitos;
        }
        return null;
    }

    private static void anexarWhatsApp(StringBuilder sb, String telefone) {
        String link = linkWhatsApp(telefone);
        if (link != null) {
            sb.append(" - WhatsApp: ").append(link);
        }
    }

    private static String texto(String valor, String fallback) {
        return valor == null || valor.isBlank() ? fallback : valor.trim();
    }
}
