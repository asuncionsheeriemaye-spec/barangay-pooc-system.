-- =============================================================
-- Barangay Resident Information System - Database Schema
-- Run this entire script in: Supabase Dashboard > SQL Editor
-- =============================================================

-- 1. Create the admin users table
--    The password column stores a BCrypt hash — NEVER plaintext.
CREATE TABLE IF NOT EXISTS users (
    username   VARCHAR(50)  PRIMARY KEY,
    password   VARCHAR(255) NOT NULL   -- stores BCrypt hash (60 chars)
);

-- 2. Create the residents table
CREATE TABLE IF NOT EXISTS residents (
    id         SERIAL       PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    age        INT          NOT NULL,
    contact    VARCHAR(50)
);

-- 3. Insert default admin account with a BCrypt-hashed password.
--
--    Username : admin
--    Password : admin123   ← what you type at login
--    Stored   : BCrypt hash of "admin123" (cost factor 12)
--
--    ⚠️  Change this password after first login in a real deployment!
--    To generate a new hash, run in Java:
--        String hash = BCrypt.hashpw("newPassword", BCrypt.gensalt());
--    or use an online BCrypt tool (cost 12) and paste the result below.
INSERT INTO users (username, password)
VALUES (
    'admin',
    '$2a$12$G1Sg/f6I/uob6hZ0hpoMCeGpkd.HMRFUg6c5mbpdKY.XmIPx2acHi'
)
ON CONFLICT (username) DO NOTHING;

-- 4. Sample resident data (optional — delete if not needed)
INSERT INTO residents (name, age, contact) VALUES
    ('Juan dela Cruz',  45, '09171234567'),
    ('Maria Santos',    32, '09281234567'),
    ('Pedro Reyes',     60, '09391234567')
ON CONFLICT DO NOTHING;
