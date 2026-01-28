ALTER TABLE refresh_tokens
    ADD CONSTRAINT uc_refresh_tokens_user_id UNIQUE (user_id);