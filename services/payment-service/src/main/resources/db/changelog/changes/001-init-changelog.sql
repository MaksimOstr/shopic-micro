CREATE TABLE payments
(
    id                     UUID        DEFAULT gen_random_uuid() PRIMARY KEY NOT NULL,
    order_id               UUID                                              NOT NULL UNIQUE,
    user_id                UUID                                              NOT NULL,
    total_in_smallest_unit BIGINT                                            NOT NULL,
    amount                 DECIMAL(10, 2)                                    NOT NULL,
    stripe_payment_id      TEXT UNIQUE,
    session_id             TEXT                                              NOT NULL UNIQUE,
    status                 VARCHAR(20)                                       NOT NULL,
    created_at             TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP             NOT NULL
);