package com.tassisfernando.chamegoapi.application.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public record AniversarianteResumo(
        UUID id,
        String nome,
        String telefone,
        LocalDate dataNascimento,
        int diasAteAniversario,
        boolean hoje) {

    public static AniversarianteResumo de(Cliente cliente, LocalDate referencia) {
        int dias = diasAteProximoAniversario(cliente.dataNascimento(), referencia);
        return new AniversarianteResumo(
                cliente.id(),
                cliente.nome(),
                cliente.telefone(),
                cliente.dataNascimento(),
                dias,
                dias == 0);
    }

    public static AniversarianteResumo recente(Cliente cliente, LocalDate referencia) {
        int dias = diasDesdeUltimoAniversario(cliente.dataNascimento(), referencia);
        return new AniversarianteResumo(
                cliente.id(),
                cliente.nome(),
                cliente.telefone(),
                cliente.dataNascimento(),
                -dias,
                dias == 0);
    }

    public static int diasAteProximoAniversario(LocalDate nascimento, LocalDate referencia) {
        LocalDate proximo = aniversarioNoAno(nascimento, referencia.getYear());
        if (proximo.isBefore(referencia)) {
            proximo = aniversarioNoAno(nascimento, referencia.getYear() + 1);
        }
        return (int) ChronoUnit.DAYS.between(referencia, proximo);
    }

    public static int diasDesdeUltimoAniversario(LocalDate nascimento, LocalDate referencia) {
        LocalDate ultimo = aniversarioNoAno(nascimento, referencia.getYear());
        if (ultimo.isAfter(referencia)) {
            ultimo = aniversarioNoAno(nascimento, referencia.getYear() - 1);
        }
        return (int) ChronoUnit.DAYS.between(ultimo, referencia);
    }

    private static LocalDate aniversarioNoAno(LocalDate nascimento, int ano) {
        MonthDay md = MonthDay.from(nascimento);
        if (md.getMonthValue() == 2 && md.getDayOfMonth() == 29 && !LocalDate.of(ano, 1, 1).isLeapYear()) {
            return LocalDate.of(ano, 2, 28);
        }
        return md.atYear(ano);
    }
}
