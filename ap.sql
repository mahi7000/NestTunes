-- Create database if not exists (optional)
CREATE DATABASE IF NOT EXISTS ap;
USE ap;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    profile_pic VARCHAR(255);
   <-- ALTER TABLE userinfo ADD COLUMN profile_pic VARCHAR(255);

);

