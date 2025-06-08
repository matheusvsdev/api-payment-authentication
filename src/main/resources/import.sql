INSERT INTO tb_user (name, email, password) VALUES ('John Doe', 'johndoe@example.com', 'password123');
INSERT INTO tb_user (name, email, password) VALUES ('Jane Doe', 'jane@example.com', 'password123');

INSERT INTO tb_role (authority) VALUES ('CLIENT');
INSERT INTO tb_role (authority) VALUES ('ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('PERSONAL', 7500.00, 1);
INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('COMPANY', 3200.00, 2);

INSERT INTO tb_transaction (sender_id, receiver_id, amount) VALUES (1, 2, 300.50);
INSERT INTO tb_transaction (sender_id, receiver_id, amount) VALUES (2, 1, 430);