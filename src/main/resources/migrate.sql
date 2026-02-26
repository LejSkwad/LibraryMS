-- ============================================================
-- Migration: Merge borrowers into users
-- Run this ONCE before starting the application
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Step 1: Add borrower columns to users table
ALTER TABLE users
    ADD COLUMN social_number VARCHAR(255),
    ADD COLUMN phone         VARCHAR(255),
    ADD COLUMN email         VARCHAR(255),
    ADD COLUMN address       VARCHAR(255),
    ADD COLUMN registration_date DATE;

-- Step 2: Migrate borrowers data into users as role='BORROWER'
INSERT INTO users (first_name, last_name, role, social_number, phone, email, address, registration_date)
SELECT first_name, last_name, 'BORROWER', social_number, phone, email, address, registration_date
FROM borrowers;

-- Step 3: Add user_id column to transactions
ALTER TABLE transactions ADD COLUMN user_id INT;

-- Step 4: Map each transaction's borrower_id to the new user_id
UPDATE transactions t
    JOIN borrowers b ON t.borrower_id = b.id
    JOIN users u ON u.social_number = b.social_number AND u.role = 'BORROWER'
SET t.user_id = u.id;

-- Step 5: Drop old borrower_id column from transactions
ALTER TABLE transactions DROP COLUMN borrower_id;

-- Step 6: Add FK constraint for user_id on transactions
ALTER TABLE transactions
    ADD CONSTRAINT fk_transaction_user FOREIGN KEY (user_id) REFERENCES users (id);

-- Step 7: Drop the borrowers table
DROP TABLE borrowers;

SET FOREIGN_KEY_CHECKS = 1;
