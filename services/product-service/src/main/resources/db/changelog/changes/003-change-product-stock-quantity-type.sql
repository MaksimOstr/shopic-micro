ALTER TABLE products
    ALTER COLUMN stock_quantity TYPE BIGINT
        USING stock_quantity::BIGINT;