const jwt = require('jsonwebtoken')

// This function checks whether the authentication token provided
// with each request is valid.
const auth = (req, res, next) => {
    const token = req.headers.user_access_token;

    // Check if there is a token
    if (token) {
        try {
            // Verify the token
            const { user_id } = jwt.verify(token, "key")
            req.users = user_id;
        } catch (err) {
            // Invalid token
            return res.status(403).json({ message: "Invalid token", err })
        }
    } else {
        // No token
        return res.status(403).json({ message: "Auth token needed" })
    }

    return next();
}

module.exports = auth;