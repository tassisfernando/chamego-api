-- Data combinada de entrega. Nula para pedidos já existentes e para vendas sem agenda.
ALTER TABLE pedido ADD COLUMN data_entrega DATE;

CREATE INDEX ix_pedido_data_entrega ON pedido (data_entrega);
