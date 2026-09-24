require('dotenv').config();

const path = require('path');
const fs = require('fs');
const express = require('express');
const cors = require('cors');
const swaggerUi = require('swagger-ui-express');
const YAML = require('yamljs');

const db = require('./db');
const authRoutes = require('./routes/auth');
const taskRoutes = require('./routes/tasks');
const adminRoutes = require('./routes/admin');
const { errorHandler } = require('./middleware/errorHandler');

process.env.JWT_SECRET = process.env.JWT_SECRET || 'super_secret_jwt_key_12345';
process.env.DATABASE_URL = process.env.DATABASE_URL || 'postgresql://postgres:postgres@localhost:5432/todo_app';

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

const specCandidates = [
  path.join(__dirname, '..', 'api_docs', 'openapi.yaml'),
  path.join(__dirname, '..', 'api docs', 'openapi.yaml'),
];
const specPath = specCandidates.find((candidate) => fs.existsSync(candidate));

if (specPath) {
  try {
    const swaggerDocument = YAML.load(specPath);
    app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerDocument));
  } catch (e) {
    console.warn('Could not parse OpenAPI spec:', e.message);
  }
}

app.get('/health', (req, res) => {
  res.json({ success: true, message: 'OK' });
});

// Support both /api/auth and /auth
app.use('/api/auth', authRoutes);
app.use('/auth', authRoutes);

// Support both /api/tasks and /tasks
app.use('/api/tasks', taskRoutes);
app.use('/tasks', taskRoutes);

// Support both /api/admin and /admin
app.use('/api/admin', adminRoutes);
app.use('/admin', adminRoutes);

app.use(errorHandler);

// Initialize DB on boot
db.initDb().then(() => {
  app.listen(PORT, () => {
    console.log(`Server listening on http://localhost:${PORT}`);
    console.log(`Swagger UI: http://localhost:${PORT}/api-docs`);
  });
}).catch(err => {
  console.error('Failed to initialize DB:', err);
  app.listen(PORT, () => {
    console.log(`Server listening on http://localhost:${PORT} (without DB init)`);
  });
});
