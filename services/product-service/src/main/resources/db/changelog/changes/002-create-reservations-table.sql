CREATE TABLE reservations
(
    id         UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    order_id   UUID        NOT NULL UNIQUE,
    status     VARCHAR(20) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservation_items
(
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id     UUID NOT NULL REFERENCES products (id),
    quantity       INT  NOT NULL CHECK (quantity > 0),
    reservation_id UUID NOT NULL REFERENCES reservations (id)
);