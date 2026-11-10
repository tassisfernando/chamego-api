package com.tassisfernando.chamegoapi.infrastructure.persistence.cliente;

import com.tassisfernando.chamegoapi.domain.cliente.Cliente;
import com.tassisfernando.chamegoapi.domain.cliente.StatusCupom;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientePersistenceMapperTest {

    private final ClientePersistenceMapper mapper = new ClientePersistenceMapper();

    @Test
    void deveConverterClienteDeDominioParaJpaEDeVolta() {
        Cliente original = Cliente.reconstituir(
                UUID.randomUUID(),
                "Maria Irene Ferreira",
                "31988518474",
                "IPATINGA",
                "MG",
                LocalDate.of(2025, 12, 18),
                LocalDate.of(1990, 3, 23),
                StatusCupom.ENVIADO,
                null);

        ClienteJpaEntity jpa = mapper.paraJpa(original);
        Cliente reconstruido = mapper.paraDominio(jpa);

        assertEquals(original.id(), jpa.getId());
        assertEquals("ENVIADO", jpa.getStatusCupom());
        assertEquals(original.nome(), reconstruido.nome());
        assertEquals(original.statusCupom(), reconstruido.statusCupom());
        assertEquals(original.dataNascimento(), reconstruido.dataNascimento());
    }

    @Test
    void deveRetornarNuloQuandoEntidadeJpaForNula() {
        assertNull(mapper.paraDominio(null));
    }
}
