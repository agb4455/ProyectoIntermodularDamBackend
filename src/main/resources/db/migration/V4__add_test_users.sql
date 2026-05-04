-- Inserción de 4 usuarios de test
-- Utiliza pgcrypto para generar el hash BCrypt ('bf') esperado por el backend

INSERT INTO users (username, email, password_hash, role)
VALUES 
    ('test1', 'viking1@test.com', crypt('pass12345', gen_salt('bf')), 'USER'),
    ('test2', 'viking2@test.com', crypt('pass12345', gen_salt('bf')), 'USER'),
    ('test3', 'viking3@test.com', crypt('pass12345', gen_salt('bf')), 'USER'),
    ('test4', 'viking4@test.com', crypt('pass12345', gen_salt('bf')), 'USER');
