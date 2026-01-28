ALTER TABLE payments
    DROP COLUMN stripe_payment_id,
    DROP COLUMN total_in_smallest_unit;

ALTER TABLE payments
    RENAME COLUMN amount TO total
