CREATE SCHEMA bank_system;

CREATE TABLE bank_system.bank
(
    bank_id SERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL
);

CREATE TABLE bank_system.users
(
    user_id    SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255)
);

CREATE TABLE bank_system.account
(
    id             SERIAL PRIMARY KEY,
    account_number VARCHAR(255)   NOT NULL,
    currency       VARCHAR(10)    NOT NULL,
    open_date      DATE           NOT NULL,
    bank_id        INT            NOT NULL,
    user_id        INT            NOT NULL,
    balance        NUMERIC(10, 2) NOT NULL,
    FOREIGN KEY (bank_id) REFERENCES bank_system.bank (bank_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES bank_system.users (user_id) ON DELETE CASCADE
);

CREATE TABLE bank_system.transaction
(
    id           SERIAL PRIMARY KEY,
    type         TEXT,
    sender_id    INT NOT NULL,
    recipient_id INT NOT NULL,
    amount       NUMERIC(15, 2),
    timestamp    TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES bank_system.account (id),
    FOREIGN KEY (recipient_id) REFERENCES bank_system.account (id)
);

INSERT INTO bank_system.bank (name)
VALUES ('Bank A'),
       ('Bank B'),
       ('Bank C'),
       ('Bank D'),
       ('Bank E');

INSERT INTO bank_system.users (first_name, last_name, patronymic)
VALUES ('John', 'Doe', 'Smith'),
       ('Alice', 'Johnson', 'Brown'),
       ('Michael', 'Williams', 'Jones'),
       ('Emily', 'Taylor', 'Wilson'),
       ('Daniel', 'Anderson', 'Clark'),
       ('Olivia', 'Thomas', 'Lewis'),
       ('Matthew', 'Jackson', 'Walker'),
       ('Sophia', 'White', 'Roberts'),
       ('James', 'Harris', 'Davis'),
       ('Emma', 'Martin', 'Miller'),
       ('William', 'Moore', 'Wilson'),
       ('Ava', 'Thompson', 'Anderson'),
       ('Benjamin', 'Lee', 'Thomas'),
       ('Mia', 'Hall', 'Harris'),
       ('Elijah', 'Clark', 'Taylor'),
       ('Charlotte', 'Baker', 'Brown'),
       ('Christopher', 'Young', 'Moore'),
       ('Abigail', 'King', 'Jones'),
       ('Daniel', 'Wright', 'Walker'),
       ('Sofia', 'Green', 'Lewis'),
       ('Joseph', 'Hill', 'Martin');

INSERT INTO bank_system.account (account_number, currency, open_date, bank_id, user_id, balance)
VALUES ('ACC-1', 'USD', '2022-08-23', 1, 1, 5000.00),
       ('ACC-2', 'EUR', '2022-08-23', 2, 1, 7000.00),
       ('ACC-3', 'USD', '2022-08-23', 3, 2, 3000.00),
       ('ACC-4', 'EUR', '2022-08-23', 4, 2, 6000.00),
       ('ACC-5', 'USD', '2022-08-23', 5, 3, 4000.00),
       ('ACC-6', 'EUR', '2022-08-23', 1, 3, 8000.00),
       ('ACC-7', 'USD', '2022-08-23', 2, 4, 2000.00),
       ('ACC-8', 'EUR', '2022-08-23', 3, 4, 5000.00),
       ('ACC-9', 'USD', '2022-08-23', 4, 5, 7000.00),
       ('ACC-10', 'EUR', '2022-08-23', 5, 5, 9000.00),
       ('ACC-11', 'USD', '2022-08-23', 1, 6, 1500.00),
       ('ACC-12', 'EUR', '2022-08-23', 2, 6, 4500.00),
       ('ACC-13', 'USD', '2022-08-23', 3, 7, 3000.00),
       ('ACC-14', 'EUR', '2022-08-23', 4, 7, 6000.00),
       ('ACC-15', 'USD', '2022-08-23', 5, 8, 4000.00),
       ('ACC-16', 'EUR', '2022-08-23', 1, 8, 8000.00),
       ('ACC-17', 'USD', '2022-08-23', 2, 9, 2000.00),
       ('ACC-18', 'EUR', '2022-08-23', 3, 9, 5000.00),
       ('ACC-19', 'USD', '2022-08-23', 4, 10, 7000.00),
       ('ACC-20', 'EUR', '2022-08-23', 5, 10, 9000.00),
       ('ACC-21', 'USD', '2022-08-23', 1, 11, 1500.00),
       ('ACC-22', 'EUR', '2022-08-23', 2, 11, 4500.00),
       ('ACC-23', 'USD', '2022-08-23', 3, 12, 3000.00),
       ('ACC-24', 'EUR', '2022-08-23', 4, 12, 6000.00),
       ('ACC-25', 'USD', '2022-08-23', 5, 13, 4000.00),
       ('ACC-26', 'EUR', '2022-08-23', 1, 13, 8000.00),
       ('ACC-27', 'USD', '2022-08-23', 2, 14, 2000.00),
       ('ACC-28', 'EUR', '2022-08-23', 3, 14, 5000.00),
       ('ACC-29', 'USD', '2022-08-23', 4, 15, 7000.00),
       ('ACC-30', 'EUR', '2022-08-23', 5, 15, 9000.00),
       ('ACC-31', 'USD', '2022-08-23', 1, 16, 1500.00),
       ('ACC-32', 'EUR', '2022-08-23', 2, 16, 4500.00),
       ('ACC-33', 'USD', '2022-08-23', 3, 17, 3000.00),
       ('ACC-34', 'EUR', '2022-08-23', 4, 17, 6000.00),
       ('ACC-35', 'USD', '2022-08-23', 5, 18, 4000.00),
       ('ACC-36', 'EUR', '2022-08-23', 1, 18, 8000.00),
       ('ACC-37', 'USD', '2022-08-23', 2, 19, 2000.00),
       ('ACC-38', 'EUR', '2022-08-23', 3, 19, 5000.00),
       ('ACC-39', 'USD', '2022-08-23', 4, 20, 7000.00),
       ('ACC-40', 'EUR', '2022-08-23', 5, 20, 6000.00);


INSERT INTO bank_system.transaction (type, sender_id, recipient_id, amount, timestamp)
SELECT CASE WHEN t.transaction_seq % 2 = 0 THEN 'Deposit' ELSE 'Withdrawal' END,
       t.sender_id,
       t.recipient_id,
       ROUND((RANDOM() * 1000)::numeric, 2),
       NOW() - INTERVAL '1' MONTH * (t.transaction_seq % 12)
FROM (SELECT ROW_NUMBER() OVER () AS transaction_seq,
             a1.id           AS sender_id,
             a2.id           AS recipient_id
      FROM bank_system.account a1,
           bank_system.account a2
      WHERE a1.id <> a2.id
        AND a1.id <= 20
        AND a2.id <= 20
      LIMIT 200) t;


