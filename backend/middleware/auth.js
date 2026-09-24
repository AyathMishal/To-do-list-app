const jwt = require('jsonwebtoken');
const { sendError } = require('./httpError');

function authenticate(req, res, next) {
  const header = req.headers.authorization || '';
  const [scheme, token] = header.split(' ');

  if (scheme !== 'Bearer' || !token) {
    return sendError(res, 401, 'UNAUTHORIZED', 'Authentication required');
  }

  try {
    const payload = jwt.verify(token, process.env.JWT_SECRET);
    req.user = {
      id: payload.id,
      email: payload.email,
      role: payload.role,
    };
    return next();
  } catch (err) {
    return sendError(res, 401, 'UNAUTHORIZED', 'Invalid or expired token');
  }
}

module.exports = { authenticate };
