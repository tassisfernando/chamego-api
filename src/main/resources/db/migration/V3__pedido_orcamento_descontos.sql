-- Orçamentos (status ORCAMENTO) e descontos percentuais em pedidos e itens.

ALTER TABLE pedido
    DROP CONSTRAINT ck_pedido_status;

ALTER TABLE pedido
    ADD COLUMN desconto_percentual NUMERIC(5, 2) NOT NULL DEFAULT 0;

ALTER TABLE pedido
    ADD CONSTRAINT ck_pedido_status CHECK (
        status IN ('RASCUNHO', 'ORCAMENTO', 'CONFIRMADO', 'CANCELADO')
    );

ALTER TABLE pedido
    ADD CONSTRAINT ck_pedido_desconto CHECK (
        desconto_percentual >= 0 AND desconto_percentual <= 100
    );

ALTER TABLE item_pedido
    ADD COLUMN desconto_percentual NUMERIC(5, 2) NOT NULL DEFAULT 0;

ALTER TABLE item_pedido
    ADD CONSTRAINT ck_item_pedido_desconto CHECK (
        desconto_percentual >= 0 AND desconto_percentual <= 100
    );