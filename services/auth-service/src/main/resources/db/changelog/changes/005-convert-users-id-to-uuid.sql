CREATE EXTENSION IF NOT EXISTS "pgcrypto";

ALTER TABLE codes DROP CONSTRAINT IF EXISTS fk_codes_user;
ALTER TABLE codes DROP CONSTRAINT IF EXISTS uc_user_id_scope;
ALTER TABLE refresh_tokens DROP CONSTRAINT IF EXISTS fk_refresh_tokens_user;

DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS roles;

ALTER TABLE users ALTER COLUMN id DROP DEFAULT;
ALTER TABLE users ALTER COLUMN id TYPE uuid USING gen_random_uuid();
ALTER TABLE users ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE codes ALTER COLUMN user_id TYPE uuid USING gen_random_uuid();
ALTER TABLE refresh_tokens ALTER COLUMN user_id TYPE uuid USING gen_random_uuid();

ALTER TABLE codes ALTER COLUMN id DROP DEFAULT;
ALTER TABLE codes ALTER COLUMN id TYPE uuid USING gen_random_uuid();
ALTER TABLE codes ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE refresh_tokens ALTER COLUMN id DROP DEFAULT;
ALTER TABLE refresh_tokens ALTER COLUMN id TYPE uuid USING gen_random_uuid();
ALTER TABLE refresh_tokens ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE public_keys ALTER COLUMN id DROP DEFAULT;
ALTER TABLE public_keys ALTER COLUMN id TYPE uuid USING gen_random_uuid();
ALTER TABLE public_keys ALTER COLUMN id SET DEFAULT gen_random_uuid();

ALTER TABLE codes
    ADD CONSTRAINT uc_user_id_scope UNIQUE (user_id, scope);

ALTER TABLE codes
    ADD CONSTRAINT fk_codes_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE refresh_tokens
    ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

DROP SEQUENCE IF EXISTS users_seq;
DROP SEQUENCE IF EXISTS codes_seq;
DROP SEQUENCE IF EXISTS refresh_tokens_seq;
DROP SEQUENCE IF EXISTS public_keys_seq;
DROP SEQUENCE IF EXISTS roles_seq;
