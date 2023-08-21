CREATE TABLE IF NOT EXISTS pessoas (
    ID SERIAL PRIMARY KEY,
    EXTERNAL_ID VARCHAR(36),
    APELIDO VARCHAR(32) UNIQUE NOT NULL,
    NOME VARCHAR(100) NOT NULL,
    NASCIMENTO CHAR(10),
    STACKS TEXT
);

CREATE UNIQUE INDEX IF NOT EXISTS external_idx ON PESSOAS (external_id);

ALTER TABLE pessoas ADD COLUMN ts tsvector
    GENERATED ALWAYS AS (to_tsvector('portuguese', coalesce(apelido, '') || ' ' || coalesce(nome, '') || ' ' || coalesce(stacks, ''))) STORED;

CREATE INDEX ts_idx ON pessoas USING gin(ts);