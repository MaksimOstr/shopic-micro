CREATE TABLE orders
(
    id                    UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    customer_name         VARCHAR(50)    NOT NULL,
    customer_phone_number VARCHAR(30)    NOT NULL,
    delivery_type         VARCHAR(25)    NOT NULL,
    delivery_price        DECIMAL(10, 2) NOT NULL CHECK (delivery_price >= 0),
    address               VARCHAR(100)   NOT NULL,
    user_id               UUID           NOT NULL,
    status                VARCHAR(20)    NOT NULL,
    comment               TEXT,
    total_price           DECIMAL(10, 2) NOT NULL CHECK (total_price >= 0),
    created_at            TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (customer_phone_number ~ '^\+?[0-9]{7,15}$')
);

CREATE TABLE order_items
(
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id        UUID           NOT NULL,
    product_name      TEXT           NOT NULL,
    product_image_url TEXT           NOT NULL,
    order_id          UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    price_at_purchase DECIMAL(10, 2) NOT NULL CHECK (price_at_purchase >= 0),
    quantity          INT            NOT NULL CHECK (quantity > 0),

    CONSTRAINT uk_order_items_order_product
        UNIQUE (order_id, product_id)
);