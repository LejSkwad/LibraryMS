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
    member_id         VARCHAR(20)  ,
    email             VARCHAR(255),
    password          VARCHAR(255),
    first_name        VARCHAR(100) NOT NULL,
    last_name         VARCHAR(100) NOT NULL,
    role              ENUM('ADMIN', 'LIBRARIAN', 'BORROWER') NOT NULL,
    phone             VARCHAR(20)  NOT NULL,
    address           TEXT,
    registration_date DATE         NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_member_id (member_id),
    UNIQUE KEY uq_users_email (email),
    CONSTRAINT chk_users_email    CHECK (email IS NULL OR email LIKE '%@%.%'),
    CONSTRAINT chk_users_password CHECK (email IS NULL OR password IS NOT NULL)
);

-- ------------------------------------------------------------
-- books
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS books (
    id                 INT          NOT NULL AUTO_INCREMENT,
    isbn               VARCHAR(20),
    title              VARCHAR(255) NOT NULL,
    author             VARCHAR(255) NOT NULL,
    publisher          VARCHAR(255) NOT NULL,
    published_year     INT,
    category           INT,
    quantity           INT          NOT NULL DEFAULT 0,
    available_quantity INT          NOT NULL DEFAULT 0,
    description        TEXT,
    cover_image        VARCHAR(500),
    page_count         INT,
    PRIMARY KEY (id),
    UNIQUE KEY uq_books_isbn (isbn),
    CONSTRAINT fk_books_category        FOREIGN KEY (category) REFERENCES category (id) ON DELETE SET NULL,
    CONSTRAINT chk_books_published_year CHECK (published_year IS NULL OR published_year BETWEEN 1000 AND 2100),
    CONSTRAINT chk_books_quantity       CHECK (quantity >= 0),
    CONSTRAINT chk_books_avail          CHECK (available_quantity >= 0 AND available_quantity <= quantity),
    CONSTRAINT chk_books_page_count     CHECK (page_count IS NULL OR page_count > 0)
);

-- ------------------------------------------------------------
-- transactions
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transactions (
    id          INT  NOT NULL AUTO_INCREMENT,
    user_id     INT  NOT NULL,
    borrow_date DATE NOT NULL,
    due_date    DATE NOT NULL,
    return_date DATE,
    status      ENUM('BORROWED', 'RETURNED') NOT NULL DEFAULT 'BORROWED',
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user  FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT chk_txn_due_date      CHECK (due_date > borrow_date),
    CONSTRAINT chk_txn_return_date   CHECK (return_date IS NULL OR return_date >= borrow_date)
);

-- ------------------------------------------------------------
-- transaction_items
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS transaction_items (
    id             INT NOT NULL AUTO_INCREMENT,
    transaction_id INT NOT NULL,
    book_id        INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_txitem (transaction_id, book_id),
    CONSTRAINT fk_txitem_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (id) ON DELETE CASCADE,
    CONSTRAINT fk_txitem_book        FOREIGN KEY (book_id)        REFERENCES books (id)
);

-- ------------------------------------------------------------
-- borrow_requests
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS borrow_requests (
    id           INT  NOT NULL AUTO_INCREMENT,
    user_id      INT  NOT NULL,
    request_date DATE NOT NULL,
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
    UNIQUE KEY uq_britem (request_id, book_id),
    CONSTRAINT fk_britem_request FOREIGN KEY (request_id) REFERENCES borrow_requests (id) ON DELETE CASCADE,
    CONSTRAINT fk_britem_book    FOREIGN KEY (book_id)    REFERENCES books (id)
);

-- ============================================================
-- Seed Data
-- All user passwords = 123456
-- ============================================================

-- ------------------------------------------------------------
-- category
-- ------------------------------------------------------------
INSERT INTO category (name) VALUES
    ('Văn học'),
    ('Khoa học'),
    ('Lịch sử'),
    ('Công nghệ'),
    ('Kinh tế'),
    ('Triết học'),
    ('Thiếu nhi'),
    ('Ngoại ngữ');
-- ------------------------------------------------------------
-- id=3 (LIB-00003) = walk-in account (no email/password yet)
INSERT INTO users (member_id, email, password, first_name, last_name, role, phone, address, registration_date) VALUES
    ('LIB-00001', 'admin@library.vn',   '$2a$12$/rGm/9GOb0Im4cf5UoqHYOKu15tVEBxm7XFxR3YJtyBK5YIawXFza', 'Admin',    'Hệ thống',  'ADMIN',     '0900000001', '1 Nguyễn Huệ, Q.1, TP.HCM',         '2024-01-01'),
    ('LIB-00002', 'minh@library.vn',    '$2a$12$/rGm/9GOb0Im4cf5UoqHYOKu15tVEBxm7XFxR3YJtyBK5YIawXFza', 'Minh',     'Thư viện',  'LIBRARIAN', '0900000002', '5 Lê Lợi, Q.1, TP.HCM',             '2024-01-10'),
    ('LIB-00003', NULL,                 NULL,                                                              'Nguyễn',   'Văn An',    'BORROWER',  '0912345678', '12 Trần Hưng Đạo, Q.5, TP.HCM',     '2024-02-01'),
    ('LIB-00004', 'thibinh@gmail.com',  '$2a$12$/rGm/9GOb0Im4cf5UoqHYOKu15tVEBxm7XFxR3YJtyBK5YIawXFza', 'Trần',     'Thị Bình',  'BORROWER',  '0923456789', '34 Đinh Tiên Hoàng, Bình Thạnh',    '2024-02-15'),
    ('LIB-00005', 'hoangnam@gmail.com', '$2a$12$/rGm/9GOb0Im4cf5UoqHYOKu15tVEBxm7XFxR3YJtyBK5YIawXFza', 'Lê',       'Hoàng Nam', 'BORROWER',  '0934567890', '78 Cách Mạng Tháng 8, Q.3, TP.HCM', '2024-03-01');

-- ------------------------------------------------------------
-- books
-- ------------------------------------------------------------
INSERT INTO books (isbn, title, author, publisher, published_year, category, quantity, available_quantity, description, cover_image, page_count) VALUES
    ('978-604-2-27801-5', 'Dế Mèn Phiêu Lưu Ký',         'Tô Hoài',               'NXB Kim Đồng',        1941, 7, 5, 4,
     'Cuộc phiêu lưu đầy màu sắc của chú dế mèn kiêu ngạo qua thế giới loài vật, học bài học về sự khiêm tốn và tình bạn.',
     NULL, 208),
    ('978-604-1-23456-7', 'Số Đỏ',                        'Vũ Trọng Phụng',        'NXB Văn học',         1936, 1, 4, 4,
     'Tiểu thuyết châm biếm xã hội thượng lưu Hà Nội thời Pháp thuộc qua nhân vật Xuân Tóc Đỏ cơ hội chủ nghĩa.',
     NULL, 312),
    ('978-604-2-34567-8', 'Tắt Đèn',                      'Ngô Tất Tố',            'NXB Văn học',         1939, 1, 3, 3,
     'Cuộc sống bi thảm của người nông dân Việt Nam dưới ách thực dân phong kiến qua nhân vật chị Dậu.',
     NULL, 256),
    ('978-604-1-98765-4', 'Lược Sử Thời Gian',            'Stephen Hawking',        'NXB Trẻ',             1988, 2, 4, 3,
     'Từ vụ nổ Big Bang đến lỗ đen — Stephen Hawking dẫn dắt người đọc qua những bí ẩn lớn nhất của vũ trụ theo cách dễ hiểu nhất.',
     NULL, 320),
    ('978-604-2-11223-0', 'Sapiens: Lược Sử Loài Người',  'Yuval Noah Harari',      'NXB Tri Thức',        2011, 3, 6, 6,
     'Hành trình 70.000 năm của loài người từ thời đồ đá đến kỷ nguyên hiện đại — khoa học, lịch sử và triết học đan xen.',
     NULL, 512),
    ('978-604-2-55667-1', 'Đắc Nhân Tâm',                 'Dale Carnegie',          'NXB Tổng hợp TP.HCM', 1936, 5, 8, 7,
     'Cuốn sách về nghệ thuật giao tiếp và ứng xử bán chạy nhất mọi thời đại, giúp bạn tạo ảnh hưởng và chinh phục lòng người.',
     NULL, 320),
    ('978-604-1-77889-2', 'Clean Code',                    'Robert C. Martin',       'NXB Lao Động',        2008, 4, 3, 2,
     'Hướng dẫn viết code sạch, dễ đọc và bảo trì từ chuyên gia phần mềm hàng đầu — kinh điển không thể thiếu với mọi lập trình viên.',
     NULL, 464),
    ('978-604-2-44556-9', 'Nhà Giả Kim',                   'Paulo Coelho',           'NXB Văn học',         1988, 1, 7, 5,
     'Hành trình của Santiago — một chú bé chăn cừu Tây Ban Nha — đi tìm kho báu và khám phá ý nghĩa cuộc sống.',
     NULL, 228),
    ('978-604-1-33445-3', 'Tư Duy Nhanh Và Chậm',         'Daniel Kahneman',        'NXB Trẻ',             2011, 5, 3, 3,
     'Hai hệ thống tư duy của não người — nhanh-bản năng và chậm-lý trí — và cách chúng điều khiển mọi quyết định của chúng ta.',
     NULL, 560),
    ('978-604-2-66778-4', 'Harry Potter và Hòn Đá Phù Thủy', 'J.K. Rowling',        'NXB Trẻ',             1997, 7, 5, 5,
     'Cậu bé Harry Potter khám phá ra mình là phù thủy và bước vào thế giới kỳ diệu của trường Hogwarts.',
     NULL, 432),
    (NULL,                'Tiếng Anh Thương Mại',          'Nhiều tác giả',          'NXB Đại học QG HCM',  2020, 8, 4, 4,
     'Giáo trình tiếng Anh thương mại dành cho sinh viên và người đi làm, bao gồm kỹ năng viết email, đàm phán và thuyết trình.',
     NULL, 280),
    ('978-604-1-55443-6', 'Triết Học Nhập Môn',            'Bertrand Russell',       'NXB Tri Thức',        1912, 6, 2, 2,
     'Giới thiệu các câu hỏi triết học cơ bản — từ sự tồn tại, nhận thức đến đạo đức — theo lối viết súc tích và dễ tiếp cận.',
     NULL, 184);

-- ------------------------------------------------------------
-- transactions  (user_id 3 = Văn An, user_id 4 = Thị Bình)
-- ------------------------------------------------------------
INSERT INTO transactions (user_id, borrow_date, due_date, return_date, status) VALUES
    (3, '2024-03-10', '2024-03-24', '2024-03-20', 'RETURNED'),
    (4, '2024-03-15', '2024-03-29', NULL,          'BORROWED'),
    (3, '2024-04-01', '2024-04-15', NULL,          'BORROWED');

-- ------------------------------------------------------------
-- transaction_items
-- ------------------------------------------------------------
INSERT INTO transaction_items (transaction_id, book_id) VALUES
    (1, 1), (1, 8),
    (2, 6),
    (3, 4), (3, 7);

-- ------------------------------------------------------------
-- borrow_requests  (user_id 5 = Hoàng Nam)
-- ------------------------------------------------------------
INSERT INTO borrow_requests (user_id, request_date, status) VALUES
    (5, '2024-04-05', 'PENDING'),
    (3, '2024-04-06', 'APPROVED'),
    (4, '2024-04-06', 'REJECTED');

-- ------------------------------------------------------------
-- borrow_request_items
-- ------------------------------------------------------------
INSERT INTO borrow_request_items (request_id, book_id) VALUES
    (1, 5), (1, 9),
    (2, 2),
    (3, 6), (3, 10);
