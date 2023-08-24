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
 owner_user_id BIGINT REFERENCES users(id) NOT NULL
 );

CREATE TABLE categories (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 title VARCHAR(255) NOT NULL UNIQUE
 type SMALLINT NOT NULL
 );

 INSERT INTO categories (title, type) VALUES
 ('Salary & Wages',0),
 ('Business Income',0),
 ('Freelance Income',0),
 ('Social Security & Benefits',0),
 ('Rental Income', 0),
 ('Investment Income',0),
 ('Dividends & Interest',0),
 ('Gifts & Bonuses',0),
 ('Reimbursements',0),
 ('Alimony',0),
 ('Other Income',0),
 ('Food & Dining',1),
 ('Housing & Rent',1),
 ('Utilities',1),
 ('Public Transport & Taxis',1),
 ('Car Expenses',1),
 ('Health and Medical',1),
 ('Hygiene & Personal Care',1),
 ('Entertainment & Leisure',1),
 ('Shopping & Retail',1),
 ('Vacation & Travel',1),
 ('Gifts',1),
 ('Communication & Media',1),
 ('Education',1),
 ('Debt & Loans',1),
 ('Alimony Payments',1),
 ('Savings & Investments',1),
 ('Insurance',1),
 ('Taxes',1),
 ('Fees',1),
 ('Other Expenses',1);

CREATE TABLE transactions (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 description VARCHAR(255) NOT NULL,
 amount NUMERIC NOT NULL,
 transaction_date DATE NOT NULL,
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
 next_date DATE NOT NULL,
 end_date DATE,
 order_interval SMALLINT NOT NULL,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL
 );

CREATE TABLE financial_goals (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 amount NUMERIC NOT NULL,
 financial_account_id BIGINT REFERENCES financial_accounts(id) UNIQUE NOT NULL
);

CREATE TABLE collaborators (
 id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
 financial_account_id BIGINT REFERENCES financial_accounts(id) NOT NULL,
 user_id BIGINT REFERENCES users(id) NOT NULL,
 UNIQUE (financial_account_id, user_id)
);






