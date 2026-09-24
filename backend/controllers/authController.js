const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const db = require('../db');
const { HttpError } = require('../middleware/httpError');
const { asyncHandler } = require('../middleware/errorHandler');

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const SALT_ROUNDS = 10;

function publicUser(row) {
  return {
    id: String(row.id),
    username: row.username || row.email.split('@')[0],
    email: row.email,
    role: row.role,
  };
}

function signToken(user) {
  return jwt.sign(
    { id: user.id, email: user.email, role: user.role },
    process.env.JWT_SECRET || 'super_secret_jwt_key_12345',
    { expiresIn: process.env.JWT_EXPIRES_IN || '7d' }
  );
}

const signup = asyncHandler(async (req, res) => {
  const email = String(req.body?.email || '').trim().toLowerCase();
  const password = req.body?.password;
  const username = String(req.body?.username || req.body?.name || '').trim() || email.split('@')[0];
  const role = (req.body?.role && ['user', 'admin'].includes(req.body.role.toLowerCase())) ? req.body.role.toLowerCase() : 'user';

  if (!email || !EMAIL_REGEX.test(email)) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'A valid email is required');
  }
  if (!password || String(password).length < 6) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'Password must be at least 6 characters');
  }

  const existing = await db.query('SELECT id FROM users WHERE email = $1', [email]);
  if (existing.rowCount > 0) {
    throw new HttpError(409, 'EMAIL_ALREADY_EXISTS', 'An account with this email already exists');
  }

  const passwordHash = await bcrypt.hash(String(password), SALT_ROUNDS);
  const result = await db.query(
    `INSERT INTO users (username, email, password_hash, role)
     VALUES ($1, $2, $3, $4)
     RETURNING id, username, email, role`,
    [username, email, passwordHash, role]
  );

  const user = publicUser(result.rows[0]);
  const token = signToken(user);

  return res.status(201).json({
    success: true,
    message: 'Account created successfully',
    token: token,
    user: user,
    data: { token, user },
  });
});

const login = asyncHandler(async (req, res) => {
  const email = String(req.body?.email || '').trim().toLowerCase();
  const password = req.body?.password;

  if (!email || !password) {
    throw new HttpError(400, 'VALIDATION_ERROR', 'Email and password are required');
  }

  const result = await db.query(
    'SELECT id, username, email, password_hash, role FROM users WHERE email = $1',
    [email]
  );

  if (result.rowCount === 0) {
    throw new HttpError(401, 'UNAUTHORIZED', 'Invalid email or password');
  }

  const row = result.rows[0];
  const matches = await bcrypt.compare(String(password), row.password_hash);
  if (!matches) {
    throw new HttpError(401, 'UNAUTHORIZED', 'Invalid email or password');
  }

  const user = publicUser(row);
  const token = signToken(user);

  return res.status(200).json({
    success: true,
    message: 'Login successful',
    token: token,
    user: user,
    data: { token, user },
  });
});

module.exports = { signup, login };
