const { Pool: PgPool } = require('pg');
const { newDb } = require('pg-mem');
const bcrypt = require('bcrypt');

let activePool = null;

async function getPool() {
  if (activePool) return activePool;

  const connectionString = process.env.DATABASE_URL || 'postgresql://postgres:postgres@localhost:5432/todo_app';
  const realPool = new PgPool({ connectionString, connectionTimeoutMillis: 2000 });

  try {
    const client = await realPool.connect();
    client.release();
    console.log('Connected to real PostgreSQL server.');
    activePool = realPool;
    return activePool;
  } catch (err) {
    console.warn('Real PostgreSQL server not reached on localhost:5432. Initializing embedded PostgreSQL engine (pg-mem)...');
    const memDb = newDb();
    
    // Support SERIAL / auto increment in pg-mem
    const { Pool: MemPool } = memDb.adapters.createPg();
    activePool = new MemPool();
    return activePool;
  }
}

async function query(text, params) {
  const p = await getPool();
  return p.query(text, params);
}

async function initDb() {
  try {
    // Create users table
    await query(`
      CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(100),
        email VARCHAR(255) NOT NULL UNIQUE,
        password_hash TEXT NOT NULL,
        role VARCHAR(20) NOT NULL DEFAULT 'user'
      );
    `);

    // Create tasks table
    await query(`
      CREATE TABLE IF NOT EXISTS tasks (
        id SERIAL PRIMARY KEY,
        user_id INTEGER NOT NULL,
        title VARCHAR(200) NOT NULL,
        description TEXT,
        category VARCHAR(50),
        date VARCHAR(50),
        time VARCHAR(50),
        status VARCHAR(50) NOT NULL DEFAULT 'pending'
      );
    `);

    // Seed default admin and user if empty
    const usersCount = await query('SELECT COUNT(*) FROM users');
    const countVal = usersCount.rows && usersCount.rows.length ? parseInt(usersCount.rows[0].count || usersCount.rows[0]['COUNT(*)'] || 0, 10) : 0;
    
    if (countVal === 0) {
      const adminHash = await bcrypt.hash('admin123', 10);
      const userHash = await bcrypt.hash('user123', 10);

      const adminRes = await query(
        `INSERT INTO users (username, email, password_hash, role)
         VALUES ('admin_user', 'admin@example.com', $1, 'admin')
         RETURNING id`,
        [adminHash]
      );

      const userRes = await query(
        `INSERT INTO users (username, email, password_hash, role)
         VALUES ('john_doe', 'user@example.com', $1, 'user')
         RETURNING id`,
        [userHash]
      );

      const uId = userRes.rows[0].id;
      await query(
        `INSERT INTO tasks (user_id, title, description, category, date, time, status)
         VALUES 
           ($1, 'Fitness', 'Exercise and gym', 'Sport', '14 Sept', '6:00 - 7:30', 'completed'),
           ($1, 'Check Emails and sms', 'Review and respond to emails', 'Work', '14 Sept', '7:30 - 8:00', 'completed'),
           ($1, 'Work on Projects', 'Focus on project tasks', 'Work', '14 Sept', '8:00 - 10:00', 'completed'),
           ($1, 'Attend Meeting', 'Team meeting with client', 'Work', '14 Sept', '10:00 - 11:00', 'pending'),
           ($1, 'Lunch Break', 'Healthy lunch and rest', 'Food', '14 Sept', '13:00 - 14:30', 'pending')`,
        [uId]
      );
      console.log('PostgreSQL Database initialized and seeded with demo data.');
    }
  } catch (err) {
    console.error('Error during database initialization:', err.message);
  }
}

module.exports = {
  query,
  initDb,
  getPool,
};
