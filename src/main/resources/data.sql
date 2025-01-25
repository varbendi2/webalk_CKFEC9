INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

INSERT INTO users (username, password) VALUES 
('user', '$2a$10$W3aBjBbcEu97DvtrSnDcjuKN7S0PSNCx5zklAcIkuH2sdYj9JQLrq'), 
('admin', '$2a$10$xQmLC/xONAjApfGItUBFYuiUgwOHISUC6cJJnmZEFCOozNr8n.H8q');

INSERT INTO user_roles (user_id, role_id) VALUES 
(1, 1), 
(2, 1), 
(2, 2);

ALTER TABLE products ADD COLUMN IF NOT EXISTS quantity INT NOT NULL DEFAULT 0;

INSERT INTO products (name, price, description, quantity) VALUES 
('Product 1', 10.0, 'Description of product 1', 100), 
('Product 2', 20.0, 'Description of product 2', 50), 
('Product 3', 15.5, 'Description of product 3', 75), 
('Product 4', 30.0, 'Description of product 4', 200);
