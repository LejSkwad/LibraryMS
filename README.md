# LibraryMS

A full-stack Library Management System built with Spring Boot and React.

Backend Repo : https://github.com/LejSkwad/LibraryMS 

Frontend Repo: https://github.com/LejSkwad/LibraryMS-React

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21, Spring Boot 4, Spring Security (JWT), JPA/Hibernate |
| Frontend | React 18, Vite, React Router |
| Database | MySQL 8.0 |
| Auth | JWT (stateless) |
| Containerization | Docker, Docker Compose |

## Roles & Permissions

| Role | Permissions                                                                  |
|------|------------------------------------------------------------------------------|
| **ADMIN** | Full access — manage books, users, categories, transactions, borrow requests |
| **LIBRARIAN** | Same as admin except admin/librarian accounts management                     |
| **BORROWER** | View books/categories, create/cancel borrow requests, view own transactions  |

## Project Structure

```
LibraryMS/               ← Backend (this repo)
├── src/
├── schema.sql           ← Database schema + seed data
├── Dockerfile
├── docker-compose.yml   ← Runs BE + FE + DB together
└── .env.example

library-react/           ← Frontend (separate repo)
├── src/
│   ├── pages/
│   ├── components/
│   └── api/api.js
└── Dockerfile
```

## Seed Accounts

All passwords: `123456`

| Email | Role |
|-------|------|
| admin@library.vn | ADMIN |
| minh@library.vn | LIBRARIAN |
| thibinh@gmail.com | BORROWER |
| hoangnam@gmail.com | BORROWER |

---

## Running the Project

### Option 1 — Docker (Recommended)

> Requires: [Docker Desktop](https://www.docker.com/products/docker-desktop)

Both backend and frontend repos must be cloned in the **same parent folder**:

```
LibraryManagementSystem/
├── LibraryMS/        ← clone this repo here
└── library-react/    ← clone frontend repo here
```

**1. Clone both repos**

```bash
git clone <backend-repo-url> LibraryMS
git clone <frontend-repo-url> library-react
```

**2. Set up environment variables**

```bash
cd LibraryMS
cp .env.example .env
```

Edit `.env` with your own values:

```
DB_URL=jdbc:mysql://db:3306/LibraryMS
DB_USERNAME=root
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_min_32_characters
```

**3. Start everything**

```bash
docker compose up --build
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:3000 |
| Backend API | http://localhost:8080 |

> First run takes a few minutes to download images and build. The database is created and seeded automatically.

**Stop:**
```bash
docker compose down
```

**Reset database:**
```bash
docker compose down -v
```

---

### Option 2 — Run Locally (Without Docker)

> Requires: Java 21, Maven, MySQL 8.0, Node.js

**Backend**

1. Create a MySQL database named `LibraryMS`
2. Run `schema.sql` to create tables and seed data
3. Start the backend:

```bash
./mvnw spring-boot:run
```

Backend runs at `http://localhost:8080`

**Frontend**

```bash
cd library-react
npm install
npm run dev
```

Frontend runs at `http://localhost:5173`

---

## API Overview

| Endpoint | Description |
|----------|-------------|
| `POST /v1/auth/login` | Login |
| `POST /v1/auth/register` | Register |
| `GET /v1/books` | List books |
| `GET /v1/category` | List categories |
| `GET /v1/users` | List users (Admin/Librarian) |
| `GET /v1/transactions` | List transactions |
| `GET /v1/borrow-requests` | List borrow requests |
| `GET /events` | SSE notifications |

Full API docs available at `http://localhost:8080/swagger-ui` when running.
