CREATE SEQUENCE roles_seq
    START WITH 1
    INCREMENT BY 50
    CACHE 50;

CREATE TABLE roles (
    id INT PRIMARY KEY DEFAULT nextval('roles_seq'),
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id)
);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE;

INSERT INTO roles (id, role_name) VALUES (1, 'ROLE_USER');
INSERT INTO roles (id, role_name) VALUES (2, 'ROLE_ADMIN');
