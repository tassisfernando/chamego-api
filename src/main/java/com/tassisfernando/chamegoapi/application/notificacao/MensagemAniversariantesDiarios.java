package com.tassisfernando.chamegoapi.application.notificacao;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.StatusCupom;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public final class MensagemAniversariantesDiarios {

    private static final DateTimeFormatter DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MensagemAniversariantesDiarios() {}

    public static Optional<String> montar(List<Cliente> aniversariantes, LocalDate referencia) {
        if (aniversariantes == null || aniversariantes.isEmpty()) {
            return Optional.empty();
        }
        var sb = new StringBuilder();
        sb.append("🎂 Aniversariantes de hoje (").append(DATA.format(referencia)).append(")\n\n");
        for (Cliente cliente : aniversariantes) {
            sb.append("• ").append(cliente.nome());
            if (cliente.telefone() != null && !cliente.telefone().isBlank()) {
                sb.append(" — WhatsApp: ").append(linkWhatsApp(cliente.telefone()));
            }
            sb.append('\n');
            String nota = notaCrm(cliente);
            if (!nota.isBlank()) {
                sb.append("  Observação: ").append(nota).append('\n');
            }
            sb.append('\n');
        }
        sb.append("Um abraço quentinho da rotina Chamego. ✨");
        return Optional.of(sb.toString().trim());
    }

    public static String assunto(LocalDate referencia) {
        return "Chamego — aniversariantes de " + DATA.format(referencia);
    }

    static String linkWhatsApp(String telefone) {
        String digitos = telefone.replaceAll("\\D", "");
        if (digitos.startsWith("55")) {
            return "https://wa.me/" + digitos;
        }
        if (digitos.length() >= 10) {
            return "https://wa.me/55" + digitos;
        }
        return telefone;
    }

    private static String notaCrm(Cliente cliente) {
        if (cliente.observacao() != null && !cliente.observacao().isBlank()) {
            return cliente.observacao().trim();
        }
        if (cliente.statusCupom() != null && cliente.statusCupom() != StatusCupom.NAO_DEFINIDO) {
            return "Cupom: " + cliente.statusCupom().name();
        }
        return "";
    }
}