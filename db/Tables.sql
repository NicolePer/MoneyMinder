CREATE TABLE users (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 username VARCHAR(255) NOT NULL,
 email VARCHAR(255) NOT NULL UNIQUE,
 password_hash VARCHAR(255) NOT NULL
 );

CREATE TABLE financial_accounts (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 title VARCHAR(255) NOT NULL,
 description VARCHAR(255),
 balance NUMERIC NOT NULL,
 owner_user_id BIGINT REFERENCES users(id) NOT NULL
 );

CREATE TABLE categories (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 title VARCHAR(255) NOT NULL UNIQUE
 );

CREATE TABLE transactions (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 description VARCHAR(255) NOT NULL,
 amount NUMERIC NOT NULL,
 date DATE NOT NULL,
 transaction_partner VARCHAR(255) NOT NULL,
 category_id BIGINT REFERENCES categories(id) NOT NULL,
 note VARCHAR(1000),
 added_automatically boolean,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL
 );


CREATE TABLE recurring_transaction_orders (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 description VARCHAR(255) NOT NULL,
 amount NUMERIC NOT NULL,
 transaction_partner VARCHAR(255) NOT NULL,
 category_id BIGINT REFERENCES categories(id) NOT NULL,
 note VARCHAR(1000),
 last_date DATE NOT NULL,
 next_date DATE NOT NULL,
 end_date DATE,
 interval SMALLINT NOT NULL,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL
 );

CREATE TABLE financial_goals (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 amount NUMERIC NOT NULL,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL
);

CREATE TABLE collaborators (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL,
 user_id BIGINT REFERENCES users(id) NOT NULL
);




