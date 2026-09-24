const { sendError } = require('./httpError');

function requireAdmin(req, res, next) {
  const role = String(req.user?.role || '').toLowerCase();
  if (role !== 'admin') {
    return sendError(res, 403, 'FORBIDDEN', 'Admin access required');
  }
  return next();
}

module.exports = { requireAdmin };
