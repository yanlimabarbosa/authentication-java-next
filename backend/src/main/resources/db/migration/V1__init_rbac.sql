-- RBAC schema
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS menus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    password_hash VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS user_profiles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, profile_id)
);

CREATE TABLE IF NOT EXISTS permissions_menu (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    menu_id UUID NOT NULL REFERENCES menus(id) ON DELETE CASCADE,
    acessar BOOLEAN,
    criar BOOLEAN,
    editar BOOLEAN,
    deletar BOOLEAN,
    exportar BOOLEAN,
    CONSTRAINT uq_profile_menu UNIQUE (profile_id, menu_id)
);

-- Seed data
INSERT INTO menus (id, key) VALUES (gen_random_uuid(), 'usuario') ON CONFLICT DO NOTHING;
INSERT INTO profiles (id, name) VALUES (gen_random_uuid(), 'Administrador') ON CONFLICT DO NOTHING;

INSERT INTO users (id, email, name, password_hash, active)
VALUES (gen_random_uuid(), 'user@example.com', 'Demo User', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_profiles (user_id, profile_id)
SELECT u.id, p.id FROM users u, profiles p
WHERE u.email = 'user@example.com' AND p.name = 'Administrador'
ON CONFLICT DO NOTHING;

INSERT INTO permissions_menu (id, profile_id, menu_id, acessar, criar, editar, deletar, exportar)
SELECT gen_random_uuid(), p.id, m.id, TRUE, TRUE, TRUE, TRUE, TRUE
FROM profiles p, menus m
WHERE p.name = 'Administrador' AND m.key = 'usuario'
ON CONFLICT DO NOTHING;
