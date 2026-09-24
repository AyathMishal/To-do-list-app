const db = require('../db');
const { HttpError } = require('../middleware/httpError');
const { asyncHandler } = require('../middleware/errorHandler');

function mapUser(row) {
  return {
    id: String(row.id),
    username: row.username || row.email.split('@')[0],
    email: row.email,
    role: row.role,
  };
}

function mapTask(row) {
  return {
    id: String(row.id),
    userId: String(row.user_id),
    user_id: row.user_id,
    title: row.title || '',
    description: row.description || '',
    category: row.category || 'Work',
    date: row.date || '',
    timeRange: row.time || '',
    time: row.time || '',
    completed: row.status === 'completed',
    status: row.status || 'pending',
  };
}

const listUsers = asyncHandler(async (req, res) => {
  const result = await db.query(
    `SELECT id, username, email, role
     FROM users
     ORDER BY id ASC`
  );

  return res.json(result.rows.map(mapUser));
});

const listAllTasks = asyncHandler(async (req, res) => {
  const params = [];
  const filters = [];

  if (req.query.user_id) {
    const userId = Number(req.query.user_id);
    if (!Number.isInteger(userId) || userId < 1) {
      throw new HttpError(400, 'VALIDATION_ERROR', 'Invalid user_id');
    }
    params.push(userId);
    filters.push(`user_id = $${params.length}`);
  }
  if (req.query.category) {
    params.push(req.query.category);
    filters.push(`category = $${params.length}`);
  }
  if (req.query.status) {
    params.push(req.query.status);
    filters.push(`status = $${params.length}`);
  }

  const where = filters.length ? `WHERE ${filters.join(' AND ')}` : '';
  const result = await db.query(
    `SELECT id, user_id, title, description, category, date, time, status
     FROM tasks
     ${where}
     ORDER BY id ASC`,
    params
  );

  return res.json(result.rows.map(mapTask));
});

module.exports = { listUsers, listAllTasks };
