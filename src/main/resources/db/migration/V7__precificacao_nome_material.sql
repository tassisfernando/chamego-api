-- Materiais avulsos (sem insumo cadastrado) em itens de precificação.
ALTER TABLE precificacao_item ADD COLUMN nome_material VARCHAR(200);