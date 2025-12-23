CREATE TABLE categories
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(20)  NOT NULL UNIQUE,
    is_active   BOOLEAN      NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE brands
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name      VARCHAR(30) NOT NULL UNIQUE,
    is_active BOOLEAN     NOT NULL
);

CREATE TABLE products
(
    id             UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    name           VARCHAR(100)   NOT NULL,
    description    TEXT           NOT NULL,
    is_deleted     BOOLEAN        NOT NULL DEFAULT false,
    image_url      TEXT           NOT NULL,
    brand_id       UUID           NOT NULL REFERENCES brands (id) ON DELETE CASCADE,
    category_id    UUID           NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    stock_quantity INT            NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0),
    price          DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    created_at     TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE likes
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL,
    product_id UUID NOT NULL REFERENCES products (id) ON DELETE CASCADE,
    UNIQUE (user_id, product_id)
);

INSERT INTO categories (name, description, is_active)
VALUES ('Electronics', 'Category for electronics', true),
       ('Food', 'Category for food', true);

INSERT INTO brands (name, is_active)
VALUES ('Samsung', true),
       ('Nokia', true);
