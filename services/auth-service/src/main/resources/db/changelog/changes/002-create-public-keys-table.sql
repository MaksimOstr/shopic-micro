CREATE SEQUENCE public_keys_seq
    START WITH 1
    INCREMENT BY 50
    CACHE 50;

CREATE TABLE public_keys (
    id BIGINT PRIMARY KEY DEFAULT nextval('public_keys_seq'),
    public_key TEXT NOT NULL UNIQUE,
    key_id TEXT NOT NULL UNIQUE,
    algorithm VARCHAR(10) NOT NULL,
    key_size INT NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
