-- Update demo user password to BCrypt hash of "password"
UPDATE users
SET password_hash = '$2a$10$rNh/W7XZT.OO/WLu5/3RX.PnQ2/cNZfH6B7JGP3WKxcMBzU6J4EVy'
WHERE email = 'user@example.com';