create database Jwt
use Jwt

CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) NOT NULL UNIQUE,
    password NVARCHAR(255) NOT NULL,
    email NVARCHAR(100) UNIQUE,
    enabled BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);


CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE
);



CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token NVARCHAR(500) NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE oauth_providers (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE -- 'google', 'facebook'
);

CREATE TABLE user_oauth (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_id BIGINT NOT NULL,
    provider_user_id NVARCHAR(100) NOT NULL, -- ID của user bên Google/Facebook
    email NVARCHAR(100), -- email lấy từ provider (nếu có)
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (provider_id) REFERENCES oauth_providers(id) ON DELETE CASCADE,
    CONSTRAINT uq_provider_user UNIQUE (provider_id, provider_user_id) -- một user của provider chỉ map 1 lần
);

-- Thêm role mặc định
INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- Thêm provider
INSERT INTO oauth_providers (name) VALUES ('google');
INSERT INTO oauth_providers (name) VALUES ('facebook');
