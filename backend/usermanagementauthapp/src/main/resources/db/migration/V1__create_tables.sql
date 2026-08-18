-- 1. Tabela de Endereços
CREATE TABLE tb_addresses (
    id BIGSERIAL PRIMARY KEY,
    zip_code VARCHAR(9) NOT NULL,
    street VARCHAR(150) NOT NULL,
    number VARCHAR(20),
    complement VARCHAR(100),
    neighborhood VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(2) NOT NULL
);

-- 2. Tabela de Usuários
CREATE TABLE tb_users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    birth_date DATE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    address_id BIGINT UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_users_address FOREIGN KEY (address_id) REFERENCES tb_addresses(id) ON DELETE SET NULL
);

-- 3. Tabela de Roles (Permissões de Acesso)
CREATE TABLE tb_roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 4. Tabela de Junção (Usuários <-> Roles)
CREATE TABLE tb_users_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_users_roles_role FOREIGN KEY (role_id) REFERENCES tb_roles(id) ON DELETE CASCADE
);

-- 5. Carga inicial com as Roles padrão do sistema
INSERT INTO tb_roles (name) VALUES ('ROLE_USER');
INSERT INTO tb_roles (name) VALUES ('ROLE_ADMIN');