INSERT INTO tb_user (name, cpf, email, password) VALUES ('John Doe', '11100033399', 'johndoe@example.com', '$2a$12$s1XiE1EIjnmISX3c/qfma.JVUjF9Y0.tfYo7R2jHQUpnviNO46UCa');
INSERT INTO tb_user (name, cpf, email, password) VALUES ('Jane Doe', '44488822211', 'jane@example.com', '$2a$12$s1XiE1EIjnmISX3c/qfma.JVUjF9Y0.tfYo7R2jHQUpnviNO46UCa');

INSERT INTO tb_role (authority) VALUES ('CLIENT');
INSERT INTO tb_role (authority) VALUES ('ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 2);

INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('PERSONAL', 7500.00, 1);
INSERT INTO tb_wallet (wallet_type, balance, user_id) VALUES ('COMPANY', 3200.00, 2);

INSERT INTO tb_transaction (sender_id, receiver_id, amount, moment) VALUES (1, 2, 300.50, '2025-05-12T15:32:27');
INSERT INTO tb_transaction (sender_id, receiver_id, amount, moment) VALUES (2, 1, 430, '2025-05-17T23:17:47');