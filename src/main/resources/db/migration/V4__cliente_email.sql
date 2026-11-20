ALTER TABLE cliente
    ADD COLUMN IF NOT EXISTS email VARCHAR(255);

CREATE INDEX IF NOT EXISTS ix_cliente_email ON cliente (email)
    WHERE email IS NOT NULL;