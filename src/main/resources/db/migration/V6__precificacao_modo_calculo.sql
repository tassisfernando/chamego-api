-- Modo de cálculo (unitário vs fracionado) e unidades adicionais (L, CM).

ALTER TABLE precificacao_item
    ADD COLUMN modo_calculo        VARCHAR(16) NOT NULL DEFAULT 'UNITARIO',
    ADD COLUMN valor_embalagem     NUMERIC(14, 2),
    ADD COLUMN quantidade_embalagem NUMERIC(14, 4),
    ADD COLUMN unidade_embalagem   VARCHAR(10),
    ADD COLUMN subtotal            NUMERIC(14, 2) NOT NULL DEFAULT 0;

ALTER TABLE precificacao_item
    ADD CONSTRAINT ck_precificacao_item_modo
        CHECK (modo_calculo IN ('UNITARIO', 'FRACIONADO'));

-- Estende unidades compatíveis em tabelas existentes (kg/g, L/ml, m/cm).
ALTER TABLE insumo DROP CONSTRAINT IF EXISTS ck_insumo_unidade;
ALTER TABLE insumo ADD CONSTRAINT ck_insumo_unidade
    CHECK (unidade_medida IN ('UN', 'ML', 'L', 'G', 'KG', 'MT', 'CM'));

ALTER TABLE ferramenta DROP CONSTRAINT IF EXISTS ck_ferramenta_unidade;
ALTER TABLE ferramenta ADD CONSTRAINT ck_ferramenta_unidade
    CHECK (unidade_medida IN ('UN', 'ML', 'L', 'G', 'KG', 'MT', 'CM'));

ALTER TABLE produto_insumo DROP CONSTRAINT IF EXISTS ck_produto_insumo_unidade;
ALTER TABLE produto_insumo ADD CONSTRAINT ck_produto_insumo_unidade
    CHECK (unidade_medida IN ('UN', 'ML', 'L', 'G', 'KG', 'MT', 'CM'));

ALTER TABLE item_consumo_producao DROP CONSTRAINT IF EXISTS ck_consumo_unidade;
ALTER TABLE item_consumo_producao ADD CONSTRAINT ck_consumo_unidade
    CHECK (unidade_medida IN ('UN', 'ML', 'L', 'G', 'KG', 'MT', 'CM'));