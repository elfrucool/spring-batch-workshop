CREATE TABLE contacts (
  id INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1) PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  email VARCHAR(75),
  phone VARCHAR(20)
);