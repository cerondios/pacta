CREATE TABLE users (
    id         VARCHAR(36)  NOT NULL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ  NOT NULL
);

CREATE TABLE user_roles (
    user_id VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role    VARCHAR(20) NOT NULL,
    PRIMARY KEY (user_id, role)
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE verification_codes (
    email      VARCHAR(255) NOT NULL PRIMARY KEY,
    code_hash  VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ  NOT NULL
);

SELECT * FROM users;
SELECT * FROM verification_codes;
SELECT * FROM user_roles;

DROP TABLE verification_codes;
DROP TABLE users;
