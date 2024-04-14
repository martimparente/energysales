-- Create the user_management schema if it doesn't exist
CREATE SCHEMA IF NOT EXISTS eco;

-- Create the users table
CREATE TABLE eco.Users
(
  id            SERIAL PRIMARY KEY,
  username      VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255)       NOT NULL,
  password_salt VARCHAR(255)       NOT NULL
);

-- Insert a sample user using bcrypt for hashing and generating salt
INSERT INTO eco.Users(username, password_hash, password_salt)
VALUES ('admin',
        '$2y$12$TAV3J6vMdNhvTVgNaypK7.9MfA/Tm/ak7AQw2a.J51XRCa/Y4aHqS',
        '$2y$12$TAV3J6vMdNhvTVgNaypK7.');
