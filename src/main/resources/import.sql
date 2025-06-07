INSERT INTO tb_user (name, email, password) VALUES ('John Doe', 'johndoe@example.com', 'password123');
INSERT INTO tb_user (name, email, password) VALUES ('Jane Doe', 'jane@example.com', 'password123');

INSERT INTO tb_role (authority) VALUES ('CLIENT');
INSERT INTO tb_role (authority) VALUES ('ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);