-- Esquema inicial da Chamego (pt-BR, snake_case).
-- Mapeia o DER (clientes, pedidos, produtos, insumos, ferramentas, despesas)
-- e a planilha operacional (estoque, produção, precificação, aniversário/cupom).

CREATE TABLE cliente (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(150) NOT NULL,
    telefone            VARCHAR(20),
    cidade              VARCHAR(80),
    estado              VARCHAR(2),
    data_cadastro       DATE NOT NULL DEFAULT CURRENT_DATE,
    data_nascimento     DATE,
    status_cupom        VARCHAR(30) NOT NULL DEFAULT 'NAO_DEFINIDO',
    observacao          TEXT,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_cliente_status_cupom CHECK (status_cupom IN (
        'NAO_DEFINIDO', 'PENDENTE_ENVIO', 'ENVIADO', 'NAO_APLICA'
    ))
);

CREATE INDEX ix_cliente_nome ON cliente (nome);
CREATE INDEX ix_cliente_telefone ON cliente (telefone);
CREATE INDEX ix_cliente_data_nascimento ON cliente (data_nascimento);

CREATE TABLE produto (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(150) NOT NULL,
    descricao           TEXT,
    categoria           VARCHAR(40) NOT NULL DEFAULT 'VELA',
    peso_gramas         INTEGER,
    preco_venda         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    custo_padrao        NUMERIC(12, 2) NOT NULL DEFAULT 0,
    estoque_atual       INTEGER NOT NULL DEFAULT 0,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_produto_categoria CHECK (categoria IN ('VELA', 'KIT', 'COMPLEMENTO')),
    CONSTRAINT ck_produto_estoque CHECK (estoque_atual >= 0),
    CONSTRAINT ck_produto_preco CHECK (preco_venda >= 0),
    CONSTRAINT uq_produto_nome UNIQUE (nome)
);

CREATE TABLE insumo (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(150) NOT NULL,
    categoria           VARCHAR(40) NOT NULL,
    marca               VARCHAR(80),
    unidade_medida      VARCHAR(10) NOT NULL,
    estoque_atual       NUMERIC(14, 3) NOT NULL DEFAULT 0,
    custo_medio         NUMERIC(12, 4) NOT NULL DEFAULT 0,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_insumo_categoria CHECK (categoria IN (
        'ESSENCIA', 'CERA', 'RECIPIENTE', 'PAVIO', 'ADESIVO',
        'EMBALAGEM', 'SPA', 'CORANTE', 'OUTRO'
    )),
    CONSTRAINT ck_insumo_unidade CHECK (unidade_medida IN ('UN', 'ML', 'G', 'KG', 'MT')),
    CONSTRAINT ck_insumo_estoque CHECK (estoque_atual >= 0)
);

CREATE UNIQUE INDEX uq_insumo_nome_marca ON insumo (nome, COALESCE(marca, ''));

CREATE TABLE ferramenta (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome                VARCHAR(150) NOT NULL,
    quantidade          NUMERIC(12, 3) NOT NULL DEFAULT 1,
    unidade_medida      VARCHAR(10) NOT NULL DEFAULT 'UN',
    valor_aquisicao     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    data_compra         DATE,
    observacao          TEXT,
    ativo               BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ferramenta_unidade CHECK (unidade_medida IN ('UN', 'ML', 'G', 'KG', 'MT'))
);

-- N-N Produtos x Insumos: receita (lista de materiais / BOM).
CREATE TABLE produto_insumo (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id          UUID NOT NULL REFERENCES produto (id),
    insumo_id           UUID NOT NULL REFERENCES insumo (id),
    quantidade          NUMERIC(14, 3) NOT NULL,
    unidade_medida      VARCHAR(10) NOT NULL DEFAULT 'G',
    CONSTRAINT ck_produto_insumo_qtd CHECK (quantidade > 0),
    CONSTRAINT ck_produto_insumo_unidade CHECK (unidade_medida IN ('UN', 'ML', 'G', 'KG', 'MT')),
    CONSTRAINT uq_produto_insumo UNIQUE (produto_id, insumo_id)
);

CREATE INDEX ix_produto_insumo_insumo ON produto_insumo (insumo_id);

CREATE TABLE pedido (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id          UUID NOT NULL REFERENCES cliente (id),
    data_pedido         DATE NOT NULL DEFAULT CURRENT_DATE,
    status              VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    natureza            VARCHAR(20) NOT NULL DEFAULT 'VENDA',
    observacao          TEXT,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_pedido_status CHECK (status IN ('RASCUNHO', 'CONFIRMADO', 'CANCELADO')),
    CONSTRAINT ck_pedido_natureza CHECK (natureza IN ('VENDA', 'DOACAO', 'PARCERIA', 'CONTRIBUICAO'))
);

CREATE INDEX ix_pedido_cliente ON pedido (cliente_id);
CREATE INDEX ix_pedido_data ON pedido (data_pedido);
CREATE INDEX ix_pedido_status ON pedido (status);

-- N-N Pedidos x Produtos.
CREATE TABLE item_pedido (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pedido_id           UUID NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    produto_id          UUID NOT NULL REFERENCES produto (id),
    quantidade          INTEGER NOT NULL,
    preco_unitario      NUMERIC(12, 2) NOT NULL,
    custo_unitario      NUMERIC(12, 2) NOT NULL DEFAULT 0,
    CONSTRAINT ck_item_pedido_qtd CHECK (quantidade > 0),
    CONSTRAINT ck_item_pedido_preco CHECK (preco_unitario >= 0)
);

CREATE INDEX ix_item_pedido_pedido ON item_pedido (pedido_id);
CREATE INDEX ix_item_pedido_produto ON item_pedido (produto_id);

-- Despesa liga-se a insumo (N-1) e/ou ferramenta (N-1), conforme o DER.
CREATE TABLE despesa (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    descricao           VARCHAR(200) NOT NULL,
    data_despesa        DATE NOT NULL DEFAULT CURRENT_DATE,
    valor               NUMERIC(12, 2) NOT NULL,
    quantidade          NUMERIC(14, 3),
    unidade_medida      VARCHAR(10),
    tipo                VARCHAR(20) NOT NULL,
    insumo_id           UUID REFERENCES insumo (id),
    ferramenta_id       UUID REFERENCES ferramenta (id),
    observacao          TEXT,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    atualizado_em       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_despesa_tipo CHECK (tipo IN ('INSUMO', 'FERRAMENTA', 'OPERACIONAL', 'MARKETING')),
    CONSTRAINT ck_despesa_valor CHECK (valor >= 0),
    CONSTRAINT ck_despesa_vinculo CHECK (
        (tipo = 'INSUMO' AND insumo_id IS NOT NULL)
        OR (tipo = 'FERRAMENTA' AND ferramenta_id IS NOT NULL)
        OR (tipo IN ('OPERACIONAL', 'MARKETING'))
    )
);

CREATE INDEX ix_despesa_data ON despesa (data_despesa);
CREATE INDEX ix_despesa_insumo ON despesa (insumo_id);
CREATE INDEX ix_despesa_ferramenta ON despesa (ferramenta_id);

-- Produção (planilha PRODUÇÃO): gera estoque de produto e consome insumos.
CREATE TABLE lote_producao (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    produto_id              UUID NOT NULL REFERENCES produto (id),
    data_producao           DATE NOT NULL DEFAULT CURRENT_DATE,
    quantidade_produzida    INTEGER NOT NULL,
    custo_unitario          NUMERIC(12, 2) NOT NULL DEFAULT 0,
    custo_total             NUMERIC(12, 2) NOT NULL DEFAULT 0,
    observacao              TEXT,
    criado_em               TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_lote_producao_qtd CHECK (quantidade_produzida > 0)
);

CREATE INDEX ix_lote_producao_produto ON lote_producao (produto_id);
CREATE INDEX ix_lote_producao_data ON lote_producao (data_producao);

CREATE TABLE item_consumo_producao (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lote_producao_id    UUID NOT NULL REFERENCES lote_producao (id) ON DELETE CASCADE,
    insumo_id           UUID NOT NULL REFERENCES insumo (id),
    quantidade          NUMERIC(14, 3) NOT NULL,
    unidade_medida      VARCHAR(10) NOT NULL DEFAULT 'G',
    custo_unitario      NUMERIC(12, 4) NOT NULL DEFAULT 0,
    CONSTRAINT ck_consumo_qtd CHECK (quantidade > 0),
    CONSTRAINT ck_consumo_unidade CHECK (unidade_medida IN ('UN', 'ML', 'G', 'KG', 'MT'))
);

CREATE INDEX ix_consumo_lote ON item_consumo_producao (lote_producao_id);

-- Auditoria de estoque (insumo ou produto acabado).
CREATE TABLE movimento_estoque (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo                VARCHAR(10) NOT NULL,
    alvo                VARCHAR(10) NOT NULL,
    insumo_id           UUID REFERENCES insumo (id),
    produto_id          UUID REFERENCES produto (id),
    quantidade          NUMERIC(14, 3) NOT NULL,
    origem              VARCHAR(20) NOT NULL,
    referencia_id       UUID,
    data_movimento      DATE NOT NULL DEFAULT CURRENT_DATE,
    criado_em           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_movimento_tipo CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    CONSTRAINT ck_movimento_alvo CHECK (alvo IN ('INSUMO', 'PRODUTO')),
    CONSTRAINT ck_movimento_origem CHECK (origem IN (
        'COMPRA', 'PRODUCAO', 'VENDA', 'AJUSTE', 'CANCELAMENTO'
    )),
    CONSTRAINT ck_movimento_alvo_fk CHECK (
        (alvo = 'INSUMO' AND insumo_id IS NOT NULL AND produto_id IS NULL)
        OR (alvo = 'PRODUTO' AND produto_id IS NOT NULL AND insumo_id IS NULL)
    )
);

CREATE INDEX ix_movimento_data ON movimento_estoque (data_movimento);
CREATE INDEX ix_movimento_insumo ON movimento_estoque (insumo_id);
CREATE INDEX ix_movimento_produto ON movimento_estoque (produto_id);
