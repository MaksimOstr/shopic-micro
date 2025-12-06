CREATE SEQUENCE codes_seq
    START WITH 1
    INCREMENT BY 50
    CACHE 50;

CREATE TABLE codes (
    id BIGINT PRIMARY KEY DEFAULT nextval('codes_seq'),
    user_id BIGINT NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    scope VARCHAR(20) NOT NULL
);

ALTER TABLE codes
    ADD CONSTRAINT uc_user_id_scope UNIQUE (user_id, scope);

ALTER TABLE codes
    ADD CONSTRAINT fk_codes_user
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;
