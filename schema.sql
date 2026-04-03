-- ============================================================
-- LibraryMS Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS LibraryMS CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE LibraryMS;

-- ------------------------------------------------------------
-- category
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS category (
    id   INT          NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_category_name (name)
);

-- ------------------------------------------------------------
-- users
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id                INT          NOT NULL AUTO_INCREMENT,
    social_number     VARCHAR(20)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    role              ENUM('ADMIN', 'LIBRARIAN', 'BORROWER') NOT NULL,
    phone             VARCHAR(20)  NOT NULL,
    address           TEXT,
    registration_date DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_social_number (social_number)
);

-- ------------------------------------------------------------
-- books
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    id                 INT          NOT NULL AUTO_INCREMENT,
    title              VARCHAR(255) NOT NULL,
    author             VARCHAR(255) NOT NULL,
    publisher          VARCHAR(255),
    published_year     INT,
    category           INT,
    quantity           INT          NOT NULL DEFAULT 0,
    available_quantity INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_books_category FOREIGN KEY (category) REFERENCES category (id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- transactions
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id          INT         NOT NULL AUTO_INCREMENT,
    user_id     INT         NOT NULL,
    social_number VARCHAR(20) NOT NULL,
    borrow_date DATE        NOT NULL,
    due_date    DATE        NOT NULL,
    return_date DATE,
    status      ENUM('BORROWED', 'RETURNED') NOT NULL DEFAULT 'BORROWED',
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ------------------------------------------------------------
-- transaction_items
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transaction_items (
    id             INT NOT NULL AUTO_INCREMENT,
    transaction_id INT NOT NULL,
    book_id        INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_txitem_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id) ON DELETE CASCADE,
    CONSTRAINT fk_txitem_book        FOREIGN KEY (book_id)        REFERENCES books (id)
);

-- ------------------------------------------------------------
-- borrow_requests
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS borrow_requests (
    id           INT      NOT NULL AUTO_INCREMENT,
    user_id      INT      NOT NULL,
    request_date DATE     NOT NULL,
    status       ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    CONSTRAINT fk_borrow_request_user FOREIGN KEY (user_id) REFERENCES users (id)
);

-- ------------------------------------------------------------
-- borrow_request_items
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS borrow_request_items (
    id         INT NOT NULL AUTO_INCREMENT,
    request_id INT NOT NULL,
    book_id    INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_britem_request FOREIGN KEY (request_id) REFERENCES borrow_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_britem_book    FOREIGN KEY (book_id)    REFERENCES books (id)
);
