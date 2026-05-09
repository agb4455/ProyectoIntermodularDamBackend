-- Añadir columna de baneo a la tabla de usuarios
ALTER TABLE users ADD COLUMN is_banned BOOLEAN NOT NULL DEFAULT FALSE;
