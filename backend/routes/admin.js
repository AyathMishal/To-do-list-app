const express = require('express');
const { authenticate } = require('../middleware/auth');
const { requireAdmin } = require('../middleware/admin');
const { listUsers, listAllTasks } = require('../controllers/adminController');

const router = express.Router();

router.use(authenticate, requireAdmin);

router.get('/users', listUsers);
router.get('/tasks', listAllTasks);

module.exports = router;
