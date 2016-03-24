CREATE TABLE contacts (
  id    SERIAL PRIMARY KEY,
  name  VARCHAR(50) NOT NULL,
  email VARCHAR(75),
  phone VARCHAR(20)
);
