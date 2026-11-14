# chamego-api

API Spring Boot da Chamego (velas artesanais).

## Perfis

| Perfil | Banco | Uso |
|--------|-------|-----|
| *(padrão)* | PostgreSQL | produção / Render + Supabase |
| `local` | **H2 em memória** | testes locais sem Postgres |

## Rodar local com H2

Perfil `local` usa H2 em memória + schema gerado pelas entidades JPA (Flyway desligado). Ideal para testar a API/frontend sem Postgres.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

- API: `http://localhost:8080`
- Console H2: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:chamego`
  - User: `sa`
  - Password: *(vazio)*

Os dados somem ao desligar a aplicação.

## Rodar com PostgreSQL

```powershell
mvn spring-boot:run
```

Usa Flyway em `db/migration`. Variáveis opcionais: `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`.
