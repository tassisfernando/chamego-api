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

    public static String assunto(LocalDate referencia) {
        return "Chamego — resumo do dia " + DIA.format(referencia);
    }

    public static Optional<String> montarHtml(
            String nomeProprietaria,
            LocalDate referencia,
            List<AniversarianteLinha> aniversariantes,
            List<EntregaLinha> entregas) {
        boolean temAniversario = aniversariantes != null && !aniversariantes.isEmpty();
        boolean temEntrega = entregas != null && !entregas.isEmpty();
        if (!temAniversario && !temEntrega) {
            return Optional.empty();
        }
        var moeda = java.text.NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        String nome = nomeProprietaria == null || nomeProprietaria.isBlank() ? "Maressa" : nomeProprietaria.trim();
        var body = new StringBuilder();
        body.append("<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\"></head><body>");
        body.append("<div style=\"font-family:Segoe UI,Arial,sans-serif;line-height:1.5;color:#2d2a26;\">");
        body.append("<p>Bom dia, <strong>")
                .append(escapar(nome))
                .append("</strong>! 🕯️✨ Aqui está o resumo da Chamego para hoje (")
                .append(DIA.format(referencia))
                .append("):</p>");
        if (temAniversario) {
            body.append("<h3 style=\"margin:1em 0 0.4em;\">🎂 Aniversariantes de Hoje</h3><ul>");
            for (AniversarianteLinha linha : aniversariantes) {
                body.append("<li><strong>")
                        .append(escapar(texto(linha.nome(), "Cliente")))
                        .append("</strong>");
                appendLinkWhatsAppHtml(body, linha.telefone());
                body.append("</li>");
            }
            body.append("</ul>");
        }
        if (temEntrega) {
            body.append("<h3 style=\"margin:1em 0 0.4em;\">📦 Entregas Agendadas para Hoje</h3><ul>");
            for (EntregaLinha linha : entregas) {
                body.append("<li>Pedido #")
                        .append(linha.pedidoId())
                        .append(" — <strong>")
                        .append(escapar(texto(linha.clienteNome(), "Cliente")))
                        .append("</strong> (")
                        .append(escapar(texto(linha.resumo(), "itens")))
                        .append(" / ")
                        .append(moeda.format(linha.valor() == null ? BigDecimal.ZERO : linha.valor()))
                        .append(")");
                appendLinkWhatsAppHtml(body, linha.telefone());
                body.append("</li>");
            }
            body.append("</ul>");
        }
        body.append("<p style=\"margin-top:1.5em;font-size:0.9em;color:#666;\">");
        body.append("Com carinho, rotina Chamego. 🕯️✨</p></div></body></html>");
        return Optional.of(body.toString());
    }

    private static void appendLinkWhatsAppHtml(StringBuilder body, String telefone) {
        String link = linkWhatsApp(telefone);
        if (link != null) {
            body.append(" — <a href=\"")
                    .append(link)
                    .append("\" style=\"color:#128C7E;\">Abrir WhatsApp</a>");
        }
    }

    private static String escapar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
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
