-- Añadir columna de rol a la tabla de usuarios
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- Actualizar el usuario administrador por defecto
UPDATE users SET role = 'ADMIN' WHERE username = 'agb445';
