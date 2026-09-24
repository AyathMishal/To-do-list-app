const db = require('../db');
const { HttpError } = require('../middleware/httpError');
const { asyncHandler } = require('../middleware/errorHandler');

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

function parseTaskPayload(body, { partial = false } = {}) {
  const payload = {};

  if (body.title !== undefined) {
    const title = String(body.title).trim();
    if (!title) {
      throw new HttpError(400, 'VALIDATION_ERROR', 'Title is required');
    }
    payload.title = title;
  } else if (!partial) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'Title is required');
  }

  if (body.description !== undefined) {
    payload.description = body.description === null ? null : String(body.description);
  }

  if (body.category !== undefined) {
    payload.category = body.category === null ? null : String(body.category);
  }

  if (body.date !== undefined) {
    payload.date = body.date === null ? null : String(body.date);
  }

  if (body.timeRange !== undefined || body.time !== undefined) {
    payload.time = body.timeRange ?? body.time ?? null;
  }

  if (body.completed !== undefined) {
    payload.status = body.completed ? 'completed' : 'pending';
  } else if (body.status !== undefined) {
    payload.status = String(body.status);
  } else if (!partial) {
    payload.status = 'pending';
  }

  return payload;
}

const listTasks = asyncHandler(async (req, res) => {
  const params = [req.user.id];
  const filters = ['user_id = $1'];

  if (req.query.date) {
    params.push(req.query.date);
    filters.push(`date = $${params.length}`);
  }
  if (req.query.category) {
    params.push(req.query.category);
    filters.push(`category = $${params.length}`);
  }
  if (req.query.status) {
    params.push(req.query.status);
    filters.push(`status = $${params.length}`);
  }

  const result = await db.query(
    `SELECT id, user_id, title, description, category, date, time, status
     FROM tasks
     WHERE ${filters.join(' AND ')}
     ORDER BY id ASC`,
    params
  );

  // Directly return JSON array for Retrofit Call<List<Task>> compatibility
  return res.json(result.rows.map(mapTask));
});

const createTask = asyncHandler(async (req, res) => {
  const payload = parseTaskPayload(req.body);
  const result = await db.query(
    `INSERT INTO tasks (user_id, title, description, category, date, time, status)
     VALUES ($1, $2, $3, $4, $5, $6, $7)
     RETURNING id, user_id, title, description, category, date, time, status`,
    [
      req.user.id,
      payload.title,
      payload.description ?? null,
      payload.category ?? null,
      payload.date ?? null,
      payload.time ?? null,
      payload.status,
    ]
  );

  return res.status(201).json(mapTask(result.rows[0]));
});

async function getOwnedTask(taskId, userId) {
  const id = Number(taskId);
  if (!Number.isInteger(id) || id < 1) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'Invalid task id');
  }

  const result = await db.query(
    `SELECT id, user_id, title, description, category, date, time, status
     FROM tasks
     WHERE id = $1 AND user_id = $2`,
    [id, userId]
  );

  if (result.rowCount === 0) {
    throw new HttpError(404, 'NOT_FOUND', 'Task not found');
  }

  return result.rows[0];
}

const getTask = asyncHandler(async (req, res) => {
  const task = await getOwnedTask(req.params.id, req.user.id);
  return res.json(mapTask(task));
});

const updateTask = asyncHandler(async (req, res) => {
  await getOwnedTask(req.params.id, req.user.id);
  const payload = parseTaskPayload(req.body, { partial: true });

  if (Object.keys(payload).length === 0) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'No fields to update');
  }

  const columns = [];
  const values = [];
  for (const [key, value] of Object.entries(payload)) {
    values.push(value);
    columns.push(`${key} = $${values.length}`);
  }
  values.push(Number(req.params.id), req.user.id);

  const result = await db.query(
    `UPDATE tasks
     SET ${columns.join(', ')}
     WHERE id = $${values.length - 1} AND user_id = $${values.length}
     RETURNING id, user_id, title, description, category, date, time, status`,
    values
  );

  return res.json(mapTask(result.rows[0]));
});

const deleteTask = asyncHandler(async (req, res) => {
  await getOwnedTask(req.params.id, req.user.id);
  await db.query('DELETE FROM tasks WHERE id = $1 AND user_id = $2', [
    Number(req.params.id),
    req.user.id,
  ]);

  return res.json({
    success: true,
    message: 'Task deleted successfully',
  });
});

module.exports = {
  listTasks,
  createTask,
  getTask,
  updateTask,
  deleteTask,
};
