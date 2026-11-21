-- Simulações e kits de precificação (velas individuais e kits personalizados).

CREATE TABLE precificacao (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titulo                  VARCHAR(200) NOT NULL,
    tipo                    VARCHAR(24) NOT NULL,
    produto_referencia_id   UUID REFERENCES produto (id),
    produto_publicado_id    UUID REFERENCES produto (id),
    margem_percentual       NUMERIC(9, 2) NOT NULL DEFAULT 0,
    preco_venda             NUMERIC(14, 2) NOT NULL DEFAULT 0,
    custo_total             NUMERIC(14, 2) NOT NULL DEFAULT 0,
    lucro_unitario          NUMERIC(14, 2) NOT NULL DEFAULT 0,
    observacao              TEXT,
    criado_em               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_precificacao_tipo CHECK (tipo IN ('VELA_INDIVIDUAL', 'KIT'))
);

CREATE INDEX ix_precificacao_tipo ON precificacao (tipo);
CREATE INDEX ix_precificacao_criado ON precificacao (criado_em DESC);

CREATE TABLE precificacao_item (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    precificacao_id   UUID NOT NULL REFERENCES precificacao (id) ON DELETE CASCADE,
    tipo_item         VARCHAR(16) NOT NULL,
    produto_id        UUID REFERENCES produto (id),
    insumo_id         UUID REFERENCES insumo (id),
    descricao         VARCHAR(200),
    quantidade        NUMERIC(14, 4) NOT NULL,
    unidade_medida    VARCHAR(10),
    custo_unitario    NUMERIC(14, 4) NOT NULL DEFAULT 0,
    CONSTRAINT ck_precificacao_item_tipo CHECK (tipo_item IN ('INSUMO', 'PRODUTO', 'EXTRA'))
);

CREATE INDEX ix_precificacao_item_precificacao ON precificacao_item (precificacao_id);