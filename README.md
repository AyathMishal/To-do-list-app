# To-Do List Application

A full-stack task management system featuring a Node.js REST API backend, PostgreSQL database, and a Java-based Android client.

## Repository Structure

```
.
├── backend/            # Express REST API & PostgreSQL connection handling
│   ├── controllers/    # Controllers for auth, tasks, and admin operations
│   ├── db/             # Database initialization (PostgreSQL / pg-mem fallback)
│   ├── middleware/     # JWT authentication and global error handling
│   ├── routes/         # Express endpoint routing
│   ├── server.js       # Node server entry point
│   └── database_schema.sql # SQL schema for users and tasks tables
├── frontend/           # Native Android application (Java / Gradle)
│   └── app/            # Android activities, adapters, network models, and UI layouts
└── api docs/           # OpenAPI 3.0 specification (openapi.yaml)
```

## Features

- **Authentication**: User registration and login using JWT tokens and bcrypt password hashing.
- **Task Management**: Create, view, edit, and delete tasks with titles, descriptions, categories, dates, times, and completion statuses.
- **Role-Based Access**: Separate user and admin permissions.
- **Admin Tools**: Interface and API endpoints for managing app users.
- **API Documentation**: Interactive Swagger UI built from OpenAPI specifications.

## Prerequisites

- **Node.js** v18+
- **PostgreSQL** v14+ (or built-in in-memory fallback for quick testing)
- **Android Studio** with JDK 17+ and Android SDK

## Backend Setup

1. Open a terminal and move to the `backend` directory:
   ```bash
   cd backend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Create a `.env` file in the `backend/` directory (you can copy `.env.example`):
   ```env
   PORT=3000
   JWT_SECRET=your_jwt_secret_key
   DATABASE_URL=postgresql://postgres:postgres@localhost:5432/todo_app
   ```

4. Initialize the PostgreSQL database:
   Execute `database_schema.sql` on your PostgreSQL instance to set up the `users` and `tasks` tables.

5. Run the server:
   ```bash
   # Development mode (with auto-reload)
   npm run dev

   # Production mode
   npm start
   ```

   - API Base URL: `http://localhost:3000`
   - Swagger UI: `http://localhost:3000/api-docs`
   - Health Check: `http://localhost:3000/health`

## Frontend Setup (Android)

1. Open **Android Studio**.
2. Select **Open** and target the `frontend` folder.
3. Wait for Gradle synchronization to finish.
4. Verify the backend base URL in `frontend/app/src/main/java/com/example/todolistapp/network/ApiClient.java`:
   - Android Emulator: `http://10.0.2.2:3000`
   - Physical Device: `http://<YOUR_COMPUTER_IP>:3000`
5. Run the project on an emulator or connected physical Android device.

## API Endpoints Summary

### Authentication
- `POST /api/auth/register` — Create a new account
- `POST /api/auth/login` — Authenticate user & return JWT

### Task Operations (Requires Authorization Header)
- `GET /api/tasks` — List tasks for current user
- `POST /api/tasks` — Create a task
- `PUT /api/tasks/:id` — Update a task
- `DELETE /api/tasks/:id` — Remove a task

### Administrative (Requires Admin Role)
- `GET /api/admin/users` — Get list of registered users
- `DELETE /api/admin/users/:id` — Delete a user account
