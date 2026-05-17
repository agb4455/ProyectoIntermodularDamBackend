-- Agregar columna para Gloria Eterna
ALTER TABLE users ADD COLUMN gloria_eterna INTEGER NOT NULL DEFAULT 0;

-- Crear un índice descendente para mejorar el rendimiento de la consulta del ranking
CREATE INDEX idx_users_gloria_eterna ON users(gloria_eterna DESC);
