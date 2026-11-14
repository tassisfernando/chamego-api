package com.tassisfernando.chamegoapi.domain.cliente;

import com.tassisfernando.chamegoapi.domain.compartilhado.RegraDeNegocioViolada;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.MonthDay;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteTest {

    @Test
    void deveIdentificarAniversarianteDoDia() {
        Cliente cliente = Cliente.novo("Tácila Tayane", "31987068258", "IPATINGA", "MG", LocalDate.of(1990, 7, 3));

        assertTrue(cliente.fazAniversarioEm(MonthDay.of(7, 3)));
        assertEquals(StatusCupom.NAO_DEFINIDO, cliente.statusCupom());
    }

    @Test
    void deveRecusarCadastroSemNome() {
        assertThrows(
                RegraDeNegocioViolada.class,
                () -> Cliente.novo("  ", "31999999999", "IPATINGA", "MG", null));
    }
}
