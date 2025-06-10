INSERT INTO tb_user (name, cpf, email, password) VALUES ('John Doe', '11100033399', 'johndoe@example.com', '7c4a8d09ca3762af61e59520943dc26494f8941b');
INSERT INTO tb_user (name, cpf, email, password) VALUES ('Jane Doe', '44488822211', 'jane@example.com', '7c4a8d09ca3762af61e59520943dc26494f8941b');

INSERT INTO tb_role (authority) VALUES ('CLIENT');
INSERT INTO tb_role (authority) VALUES ('ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('PERSONAL', 7500.00, 1);
INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('COMPANY', 3200.00, 2);

INSERT INTO tb_transaction (sender_id, receiver_id, amount) VALUES (1, 2, 300.50);
INSERT INTO tb_transaction (sender_id, receiver_id, amount) VALUES (2, 1, 430);