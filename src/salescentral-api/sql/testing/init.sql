-- Create the users table
CREATE TABLE IF NOT EXISTS users
(
  id         SERIAL PRIMARY KEY,
  username   VARCHAR(50)  NOT NULL,
  "password" VARCHAR(255) NOT NULL,
  salt       VARCHAR(255) NOT NULL
);

-- Insert a test user
INSERT INTO users ("password", salt, username)
VALUES ('1c1b869d3e50dd3703ad4e02c5b143a8e55089fac03b442bb95398098a6e2fb4', 'c3f842f3630ebb3d96543709bc316402', 'testUser')
