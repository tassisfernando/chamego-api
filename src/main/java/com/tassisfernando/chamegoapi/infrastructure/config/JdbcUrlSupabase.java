package com.tassisfernando.chamegoapi.infrastructure.config;

/**
 * O pooler do Supabase na porta 6543 (modo Transaction / PgBouncer) descarta a sessão
 * a cada transação. O Flyway usa advisory lock do Postgres e falha com
 * "Unable to release PostgreSQL advisory lock".
 * A porta 5432 do mesmo host é o modo Session, compatível com Flyway e Hikari.
 */
public final class JdbcUrlSupabase {

    private JdbcUrlSupabase() {
    }

    public static String normalizar(String url) {
        if (url == null || url.isBlank() || !url.startsWith("jdbc:postgresql:")) {
            return url;
        }
        String ajustada = url
                .replace(":6543/", ":5432/")
                .replace(":6543?", ":5432?");
        if (ajustada.endsWith(":6543")) {
            ajustada = ajustada.substring(0, ajustada.length() - 4) + "5432";
        }
        if (!ajustada.contains("prepareThreshold=")) {
            ajustada = ajustada + (ajustada.contains("?") ? "&" : "?") + "prepareThreshold=0";
        }
        return ajustada;
    }
}
