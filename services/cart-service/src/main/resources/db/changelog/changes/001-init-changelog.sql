CREATE TABLE IF NOT EXISTS carts (
    id uuid PRIMARY KEY NOT NULL,
    user_id uuid NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cart_items (
    id uuid PRIMARY KEY NOT NULL,
    cart_id uuid NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_name TEXT NOT NULL,
    product_image_url TEXT NOT NULL,
    product_id uuid NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    price_at_add DECIMAL(10, 2) NOT NULL CHECK (price_at_add >= 0)
);

ALTER TABLE cart_items
    ADD CONSTRAINT cart_items_cart_product_unique UNIQUE (cart_id, product_id);
