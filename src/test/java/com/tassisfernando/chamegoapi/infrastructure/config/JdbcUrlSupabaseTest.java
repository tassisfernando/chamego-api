package com.tassisfernando.chamegoapi.infrastructure.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcUrlSupabaseTest {

    @Test
    void deveTrocarPoolerTransactionPorSession() {
        String url = "jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:6543/postgres?sslmode=require";

        String normalizada = JdbcUrlSupabase.normalizar(url);

        assertTrue(normalizada.contains(":5432/"));
        assertTrue(normalizada.contains("sslmode=require"));
        assertTrue(normalizada.contains("prepareThreshold=0"));
        assertEquals(
                "jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require&prepareThreshold=0",
                normalizada);
    }

    @Test
    void deveIgnorarJdbcQueNaoSejaPostgres() {
        String h2 = "jdbc:h2:mem:chamego;MODE=PostgreSQL;DB_CLOSE_DELAY=-1";
        assertEquals(h2, JdbcUrlSupabase.normalizar(h2));
    }

    @Test
    void devePreservarPortaDiretaENaoDuplicarParametro() {
        String url = "jdbc:postgresql://localhost:5432/chamego?prepareThreshold=0";

        assertEquals(url, JdbcUrlSupabase.normalizar(url));
    }
}
