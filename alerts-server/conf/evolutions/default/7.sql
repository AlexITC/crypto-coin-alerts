-- In order to support coinmarketcap, we need to store the currency name
-- to differentiate repeated currencies.
--
-- while the new column currency_name could be nullable, that have conflicts with
-- the unique constraint because PostgreSQL doesn't consider null values as equal
-- (at least when working with unique constraint), setting the empty string instead
-- give us the expected effect.

# --- !Ups

-- add the new column
ALTER TABLE currencies
ADD COLUMN currency_name CITEXT NOT NULL DEFAULT '';

-- update unique constraint to include the name
ALTER TABLE currencies
DROP CONSTRAINT currencies_unique;

ALTER TABLE currencies
ADD CONSTRAINT currencies_unique UNIQUE (exchange, market, currency, currency_name);

# --- !Downs

-- rollback unique constraint
ALTER TABLE currencies
DROP CONSTRAINT currencies_unique;

ALTER TABLE currencies
ADD CONSTRAINT currencies_unique UNIQUE (exchange, market, currency);


-- drop the name column
ALTER TABLE currencies
DROP COLUMN currency_name;
