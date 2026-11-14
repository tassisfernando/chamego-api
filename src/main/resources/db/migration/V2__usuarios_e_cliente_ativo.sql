-- Usuários do sistema (autenticação) + soft delete de clientes.

CREATE TABLE usuario (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    login           VARCHAR(80) NOT NULL,
    senha_hash      VARCHAR(100) NOT NULL,
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_usuario_login UNIQUE (login)
);

-- login: admin | senha: chamego123 (BCrypt cost 10)
INSERT INTO usuario (id, login, senha_hash, ativo, criado_em)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'admin',
    '$2a$10$ogPVhsMZOwGgkuapaXU6n.3ys/84kTDvM.m9dalMmfh6L/Dr8jQ3e',
    TRUE,
    NOW()
);

ALTER TABLE cliente
    ADD COLUMN IF NOT EXISTS ativo BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX IF NOT EXISTS ix_cliente_ativo ON cliente (ativo);
