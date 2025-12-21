CREATE TYPE order_status AS ENUM (
    'PENDING',
    'PROCESSING',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED'
    );

ALTER TABLE orders
    ALTER COLUMN status
        TYPE order_status
        USING status::order_status;